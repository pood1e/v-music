package me.pood1e.vmusic.server.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * @author pood1e
 */
@RestController
@RequestMapping("/api/local")
public class LocalController {

    @Value("${app.save-path}")
    private String path;

    @GetMapping("/{filename}")
    public Mono<Void> downloadByWriteWith(@PathVariable String filename,
                                          ServerHttpRequest request,
                                          ServerHttpResponse response) throws IOException {
        ZeroCopyHttpOutputMessage zeroCopyResponse = (ZeroCopyHttpOutputMessage) response;
        response.getHeaders().set(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=" + filename);
        response.setStatusCode(HttpStatus.PARTIAL_CONTENT);

        File file = new File(path, filename);
        long length = file.length();
        long start = 0;
        long end = length;
        List<HttpRange> range = request.getHeaders().getRange();
        if (range.size() > 0) {
            start = range.get(0).getRangeStart(length);
            end = range.get(0).getRangeEnd(length);
        }
        response.getHeaders().setContentType(MediaType.valueOf("audio/mpeg"));
        response.getHeaders().setContentLength(end - start + 1);
        response.getHeaders().set(HttpHeaders.CONTENT_RANGE, "bytes " + start + "-" + end + "/" + length);
        return zeroCopyResponse.writeWith(file, start, end - start + 1);
    }
}
