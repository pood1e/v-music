# Music

## QQ

### 搜索

```shell
curl --location --request POST 'https://u.y.qq.com/cgi-bin/musicu.fcg' \
--header 'Referer: https://y.qq.com' \
--header 'Content-Type: application/json' \
--data-raw '{
    "req": {
        "method": "DoSearchForQQMusicDesktop",
        "module": "music.search.SearchCgiService",
        "param": {
            "query": "完美生活"
        }
    }
}'
```

### 歌曲封面

```
"https://y.gtimg.cn/music/photo_new/T002R300x300M000${mid}.jpg"
```

### 歌曲信息

```shell
curl --location --request POST 'https://u.y.qq.com/cgi-bin/musicu.fcg' \
--header "Cookie: uin=${uin}; qm_keyst=${qm_cookie};" \
--header 'Content-Type: application/json' \
--data-raw '{
    "req": {
        "module": "vkey.GetVkeyServer",
        "method": "CgiGetVkey",
        "param": {
            "filename": [
                "M500001gFmS32mSRvn001gFmS32mSRvn.mp3"
            ],
            "guid": "6227871",
            "songmid": [
                "001gFmS32mSRvn"
            ],
            "platform": "20"
        }
    }
}'
```

## 网易云

### 加密

#### 生成随机密钥

```
# 16位 字母加数字 
random_key = "TRWreZVdRZLZZPiZ"
```

#### 加密数据

```
# 加密算法为 AES/CBC/PKCS5Padding
# 第一次加密密钥为 0CoJUm6Qyw8W8jud, 向量为 0102030405060708
encrypted_fixed_pass =  base64(cipher.doFinal(payload))
# 第二次加密密钥为 random_key, 向量为 0102030405060708
encrypted = base64(cipher.doFinal(encrypted_fixed_pass))
```

#### 加密随机密钥

```
# 反转密钥
reverse_key = "ZiPZZLZRdVZerWRT"
# 使用RSA/ECB/NoPadding进行加密
# 公钥为 MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDgtQn2JZ34ZC28NWYpAUd98iZ37BUrX/aKzmFbt7clFSs6sXqHauqKWqdtLkF2KexO40H1YTX8z2lSgBBOAxLsvaklV8k4cBFK9snQXE9/DDaFt6Rr7iVZMldczhC0JNgTz+SHXT6CBHuX3e9SdB1Ua44oncaTWz7OBGLbCiK45wIDAQAB
encrypted_key = hex(cipher.doFinal(reverse_key))
```

#### 加密结果

```
params=urlencode(encrypted)&encSecKey=urlencode(encrypted_key)
```

### 搜索

```shell
{
    "s": "x",
    "type": 1,
    "limit": 30,
    "offset": 0,
    "csrf_token": null
}
```

```shell
curl --location --request POST 'https://music.163.com/weapi/search/get' \
--header 'Content-Type: application/x-www-form-urlencoded' \
--data-raw 'params=e5%2F0bJFeM9IzL8emYEledfVgLQN14DpNQ5NOnhvTmh8Kz8myQOtu%2BDYVqz0rPg8rL8RwbqYlEJ%2F2rudZZeH2TpGNJJao1p3LV8Nlz%2BZh4G%2FPZe4Wmgg%2BtaKqKakKrBIH&encSecKey=de93120acef8902c20642f4fcbbfd137c637ddd49a5ef040aebf11b56eedf29162128e61e0d071f10c585b3b2742c99682fc045e5202e90fbd93f6806eb6f9afdd03d689288f4d365ecf4df3014f58c570c7f85ab2ae58ecd631f3f35719c4d425b103c882f2eb0565609afcdb8e04def6accec3f0732e132235877f76fb92ca'
```

### 歌单

```shell
curl --location --request POST 'https://music.163.com/api/v6/playlist/detail' \
--header 'Content-Type: application/x-www-form-urlencoded' \
--data-raw "id=${playlist_id}"
```

### 歌曲信息(批量)

```json
{
	"c": "[{\"id\":168087}]"
}
```

```shell
curl --location --request POST 'https://music.163.com/weapi/v3/song/detail' \
--header 'Content-Type: application/x-www-form-urlencoded' \
--data-raw 'params=0yGHZIFxOMyNc%2FCpOi%2B7PsIpEI5AI%2BrvqRExrQ4O7S%2FvUvXHEmj8TrimaHEITxIS&encSecKey=5b3db4a3db661c10dc6d1a3358f5cc9a009da7b6bf61e4f61a3aa98b193fcd0e1c423b9d0ff6ee45b1dc94e961af1173d2165473ea578009486eb03b56e41f9a5cf2c80f9faa3992a252c20b8c6481c552eea1ad17b7b303179db82aea41f4f073fe18d5c53318383b51d2e139bd16471d6e4049c01c4ccfd34fedc47a60404a
'
```

## 咪咕

```

```

