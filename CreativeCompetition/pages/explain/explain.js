// pages/explain/explain.js
const app = getApp()
const WXAPI = require('../../wxapi/main')
Page({
  data: {
    //判断小程序的API，回调，参数，组件等是否在当前版本可用。
    canIUse: wx.canIUse('button.open-type.getUserInfo'),
    name:'',
    discriptions:''
  },
  onLoad: function () {
    var that=this;
    WXAPI.explain().then(function (res) {
      var list=res.data;
      var title = list.name;
      var discriptions = list.discriptions;
      that.setData({
        name: title,
        discriptions: discriptions
      })
      
    

     
    })


  
    


  }
  
})

