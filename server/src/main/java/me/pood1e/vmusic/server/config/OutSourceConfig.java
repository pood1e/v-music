package me.pood1e.vmusic.server.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import me.pood1e.vmusic.server.core.request.kugou.KugouApi;
import me.pood1e.vmusic.server.core.request.kuwo.KuwoApi;
import me.pood1e.vmusic.server.core.request.migu.MiguApi;
import me.pood1e.vmusic.server.core.request.netease.NeteaseApi;
import me.pood1e.vmusic.server.core.request.tencent.TencentApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import org.springframework.web.util.DefaultUriBuilderFactory;
import reactor.netty.http.client.HttpClient;

import javax.net.ssl.SSLException;

/**
 * @author pood1e
 */
@Configuration
public class OutSourceConfig {

    private Object codec() {
        Jackson2JsonDecoder decoder = new Jackson2JsonDecoder(new ObjectMapper(),
                MimeTypeUtils.parseMimeType(MediaType.TEXT_PLAIN_VALUE),
                MimeTypeUtils.parseMimeType(MediaType.TEXT_HTML_VALUE));
        decoder.setMaxInMemorySize(32 * 1024 * 1024);
        return decoder;
    }

    @Bean
    public TencentApi tencentApi() {
        WebClient client = WebClient.builder()
                .exchangeStrategies(ExchangeStrategies.builder().codecs(configurer ->
                        configurer.customCodecs().register(codec())).build())
                .build();
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builder(WebClientAdapter.forClient(client)).build();
        return factory.createClient(TencentApi.class);
    }

    @Bean
    public NeteaseApi neteaseApi() {
        WebClient client = WebClient.builder()
                .exchangeStrategies(ExchangeStrategies.builder().codecs(configurer ->
                        configurer.customCodecs().register(codec())).build())
                .build();
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builder(WebClientAdapter.forClient(client)).build();
        return factory.createClient(NeteaseApi.class);
    }

    @Bean
    public MiguApi miguApi() throws SSLException {
        SslContext sslContext = SslContextBuilder
                .forClient()
                .trustManager(InsecureTrustManagerFactory.INSTANCE)
                .build();
        HttpClient httpClient = HttpClient.create().secure(t -> t.sslContext(sslContext));
        WebClient client = WebClient.builder()
                .defaultHeader(HttpHeaders.REFERER, "https://m.music.migu.cn/v3")
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builder(WebClientAdapter.forClient(client)).build();
        return factory.createClient(MiguApi.class);
    }

    @Bean
    public KugouApi kugouApi() {
        WebClient client = WebClient.builder()
                .exchangeStrategies(ExchangeStrategies.builder().codecs(configurer ->
                        configurer.customCodecs().register(codec())).build())
                .build();
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builder(WebClientAdapter.forClient(client)).build();
        return factory.createClient(KugouApi.class);
    }

    @Bean
    public KuwoApi kuwoApi() {
        WebClient client = WebClient.builder()
                .defaultHeader(HttpHeaders.COOKIE, "kw_token=a")
                .defaultHeader("csrf", "a")
                .defaultHeader(HttpHeaders.REFERER, "https://kuwo.cn")
                .build();
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builder(WebClientAdapter.forClient(client)).build();
        return factory.createClient(KuwoApi.class);
    }

    @Bean
    public WebClient webClient() {
        final int size = 32 * 1024 * 1024;
        final ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(codecs -> codecs.defaultCodecs().maxInMemorySize(size))
                .build();
        DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory();
        factory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.NONE);
        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(
                        HttpClient.create().followRedirect(true)
                ))
                .uriBuilderFactory(factory)
                .exchangeStrategies(strategies)
                .build();
    }
}
