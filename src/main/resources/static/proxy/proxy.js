const setProxy = function(){
        if(!window.ui) return // swagger-ui has not been initialized yet
        const uiConfigs = window.ui.getConfigs()
        uiConfigs.showMutatedRequest = false
        uiConfigs.requestInterceptor = (r)=>{
            if(!r.url.includes(window.location.host))
            r.url = window.location.protocol + "//" + window.location.host + "/swagger-proxy?url=" + encodeURIComponent(r.url) + "&method=" + r.method + "&headers=" + new URLSearchParams(r.headers).toString()
            console.log(r)
            return r
        }
        clearInterval(timer)
    }
const timer = setInterval(setProxy,500);