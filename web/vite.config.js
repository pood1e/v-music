import {fileURLToPath, URL} from 'node:url'

import {defineConfig} from 'vite'
import vue from '@vitejs/plugin-vue'

// https://vitejs.dev/config/
export default defineConfig({
    plugins: [vue()],
    resolve: {
        alias: {
            '@': fileURLToPath(new URL('./src', import.meta.url))
        }
    },
    build: {
        outDir: "../server/target/classes/static"
    },
    server: {
        proxy: {
            '/api': {
                target: 'http://192.168.1.3:16300/',
                changeOrigin: true,
                rewrite: (path) => path.replace(/^\/api/, '/api')
            }
        },
    },
})
