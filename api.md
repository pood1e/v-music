# Music

## QQ

### 搜索

```shell
curl --location --request POST 'https://u.y.qq.com/cgi-bin/musicu.fcg' \
--header 'Referer: https://y.qq.com' \
--header 'Content-Type: application/json' \
--data-raw '{
        "comm": {
            "ct": "19",
            "cv": "1859",
            "uin": "0"
    },
        "req": {
            "method": "DoSearchForQQMusicDesktop",
            "module": "music.search.SearchCgiService",
            "param": {
                "grp": 1,
                "num_per_page": 20,
                "page_num": 1,
                "query": "xx",
                "search_type": 0
        }
    }
}'
```

