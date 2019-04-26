// 小程序开发api接口工具包，https://github.com/gooking/wxapi
const API_BASE_URL = 'https://wxapp.zyzsbj.cn/wxappservice'


const request = (url, needSubDomain, method, data) => {
  let _url = API_BASE_URL  + url
  return new Promise((resolve, reject) => {
    wx.request({
      url: _url,
      method: method,
      data: data,
      header: {
          'Content-Type': 'application/x-www-form-urlencoded;charset=utf-8'
      },
      success(request) {
        resolve(request.data)
      },
      fail(error) {
        reject(error)
      },
      complete(aaa) {
        // 加载完成
      }
    })
  })
}

/**
 * 小程序的promise没有finally方法，自己扩展下
 */
Promise.prototype.finally = function (callback) {
  var Promise = this.constructor;
  return this.then(
    function (value) {
      Promise.resolve(callback()).then(
        function () {
          return value;
        }
      );
    },
    function (reason) {
      Promise.resolve(callback()).then(
        function () {
          throw reason;
        }
      );
    }
  );
}

module.exports = {
  request,
  login: (code) => {
    return request('/api/v1/wx/getSession', true, 'get', {
      code:code,
      appId: 'wxa192d06cd30b94b7',
      apiName: 'WX_CODE'
    })
  }, 
  checkToken: (token) => {
    return request('/api/v1/wx/checkToken', true, 'get', {
      token: token,
      appId: 'wxa192d06cd30b94b7',
      apiName: 'WX_CODE'
    })
  },
  register: (data) => {
    return request('/api/v1/wx/decodeUserInfo', true, 'get', data)
  },
  checkUserInfo: (data) => {
     return request('api/v1/wx/checkUserInfo', true, 'get', data)
    },
    wxarticles: (data) => {
        var url = '/api/v1/wxarticles/rank/' + data.sessionId+'/'+data.page
        return request(url, true, 'get', data)
    },
    getwxarticle: (data) => {
        var url = '/api/v1/getwxarticle/' + data.sessionId+'/'+data.aid
        return request(url, true, 'get', data)
    },
    zan: (data) => {
        var url = '/api/v1/wxarticles/' + data.sessionId + '/' + data.method+'/'+data.aid
        return request(url, true, 'get', data)
    },
    explain: () => {
        return request('/api/v1/get/conf', true, 'get', {
            appId: 'wxa192d06cd30b94b7',
            apiName: 'WX_GET_CONF'
        })
    },
    issue: (data) => {
         var url = '/api/v1/wxarticle/save/' + data.sessionId
         return request(url, true, 'post',data)
    },
    result: (sessionId) => {
        var url = '/api/v1/rank/result/' + sessionId
        return request(url, true, 'get', {
            sessionId: sessionId,
            appId: 'wxa192d06cd30b94b7',
            apiName: 'WX_RANK_RESULT'
        })
    },
    save: (data) => {
      var url = '/api/v1/user/address/' + data.sessionId
         return request(url, true, 'post',data)
    },
    


    
}
