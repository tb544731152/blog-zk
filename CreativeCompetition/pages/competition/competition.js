// pages/competition/competition.js
var app = getApp();
var code;
const WXAPI = require('../../wxapi/main')
Page({

    /**
     * 页面的初始数据
     */
    data: {
        time: '2019年1月20日-5月20日',
        hiddenNO: true,
        // hiddenExamine:false,
        ourselfs: [],
        ranks: [],
        total: '',
        auditNum: '',
        my_index1: 0,
        text: '点击展开',
        myCommentHidden: true,
        userCommentHidden: true,
        downHidden: true,
        isLoading: false,
        noMoreHidden: true,
        canIUse: wx.canIUse('button.open-type.getUserInfo'),
        resultRanks: '',
        resulTip: '',
        getPrize: true,
        prizeBox: true,
        introAid: '',
        findPrize: true,
        isUserAid: '',
        ismyAid: '',       
        topImg:'',
        commonScreen:true,
        findUrl:'',
        sessionId:123,
        page1:1,
        competitionName:'',
        prizeName:'',
    },


    /**
     * 生命周期函数--监听页面加载
     */
    onLoad: function(options) {
      const _this = this
      const token = wx.getStorageSync('token');
      console.log(token)
      if (!token) {
        _this.goLoginPageTimeOut()
        return
      }
      /**
       * token 检查**/
      WXAPI.checkToken(token).then(function (res) {
        if (res.code != 1000) {
          wx.removeStorageSync('token')
          _this.goLoginPageTimeOut()
        }
      })

    },

    /**
     * 生命周期函数--监听页面初次渲染完成
     */
    onReady: function() {
       
    },

    /**
     * 生命周期函数--监听页面显示
     */
    onShow: function() {
        console.log('调研 on show');
      var that = this;
      that.judgeSQ();
      that.imgFun();
    },

    /**
     * 生命周期函数--监听页面隐藏
     */
    onHide: function() {

    },

    /**
     * 生命周期函数--监听页面卸载
     */
    onUnload: function() {

    },
  goLoginPageTimeOut: function () {
    // wx.removeStorageSync('token')
    setTimeout(function () {
      wx.navigateTo({
        url: "/pages/authorize/index"
      })
    }, 1000)
  },
    /**
     * 页面相关事件处理函数--监听用户下拉动作
     */
    onPullDownRefresh: function() {
      wx.stopPullDownRefresh();
      const _this = this
      const token = wx.getStorageSync('token');
      console.log(token)
      if (!token) {
        _this.goLoginPageTimeOut()
        return
      }
      /**
       * token 检查**/
      WXAPI.checkToken(token).then(function (res) {
        if (res.code != 1000) {
          wx.removeStorageSync('token')
          _this.goLoginPageTimeOut()
        }
      })
      var that = this ;
        wx.showLoading({
            title: '加载中',
        })
        this.setData({
                page1 : 1,
                ourselfs: [],
                ranks: [],
                noMoreHidden: true,
        })
        setTimeout(function() {
            that.onShow();
            wx.hideLoading();
        }, 2000);

    },

    /**
     * 页面上拉触底事件的处理函数
     */
    onReachBottom: function() {
      const _this = this
      const token = wx.getStorageSync('token');
      if (!token) {
        _this.goLoginPageTimeOut()
        return
      }
      /**
       * token 检查**/
      WXAPI.checkToken(token).then(function (res) {
        console.log(res)
        if (res.code != 1000) {
          wx.removeStorageSync('token')
          _this.goLoginPageTimeOut()
        }
      })
        wx.showLoading({
          title: '加载中',
        })
        setTimeout(function () {
          _this.dataList1()
          wx.hideLoading();
        }, 2000);
        /**
        var isLoading = this.data.isLoading;
        var rankslen = this.data.ranks.length;
        var page1 = this.data.page1;
        var that=this;
        if (isLoading == true && rankslen >= 10) {
            page1 = page1 + 1;
            that.setData({
                page1: page1
            })
            
        }
         */
    },

    /**
     * 用户点击右上角分享
     */
    onShareAppMessage: function() {

    },
    //数据来源
    dataList: function() {
        var page1 = this.data.page1;
        var that = this;
        var sessionId = that.data.sessionId; 
        WXAPI.wxarticles({
            page: page1,
            sessionId: sessionId,
            apiName: 'WX_GET_ARTICLE',
            appId: 'wxa192d06cd30b94b7'
        }).then(function(res) {
            console.log(res.data);
            if (sessionId != 123) {
                //登录后看到的数据           
              if (res.data.ourselfs !=null && res.data.ourselfs.length != 0) {
                    var ourselfs = res.data.ourselfs;
                    var num1 = 0;            
                    for (var i = 0; i < ourselfs.length; i++) {                   
                        if (ourselfs[i].art.status == 0) {
                            num1 = num1 + 1;                        

                        }
                        that.setData({
                            auditNum: num1
                        })

                    }
                    that.setData({
                        myCommentHidden: false,
                        ourselfs: ourselfs,
                        total: ourselfs.length,
                        hiddenNO: true,

                    })
                    
                    if (ourselfs.length > 1) {
                        that.setData({
                            downHidden: false,
                        })                
                    }

                } else {
                    that.setData({
                        myCommentHidden: true
                    })
                }
              if (res.data.ranks!=null && res.data.ranks.length != 0) {
                    var ranks1 = res.data.ranks;           
                    that.setData({
                        userCommentHidden: false,
                        ranks: ranks1,
                        isLoading: true,
                        hiddenNO: true,
                    })
                  

                } else {
                    that.setData({
                        userCommentHidden: true
                    })
                }
              if (res.data.ourselfs!=null && res.data.ourselfs.length == 0 && res.data.ranks.length == 0) {
                    that.setData({
                        hiddenNO: false,
                    })
                }
            } else {
                //没有登录看到的数据
                console.log('用户没有登录');
                if (res.data.ranks.length != 0) {
                    var ranks1 = res.data.ranks;
                    that.setData({
                        userCommentHidden: false,
                        ranks: ranks1,
                        isLoading: true,
                        hiddenNO: true,
                    })
           

                } else {
                    that.setData({
                        userCommentHidden: true,
                        hiddenNO: false,
                    })
                }
            }



            wx.stopPullDownRefresh();

        })

    },

    //loading加载数据
    dataList1: function() {      
        var that = this;
        var page1 = this.data.page1+1;
        that.setData({
          page1: page1
        })
        var sessionId = that.data.sessionId;
        var page1 = that.data.page1;
        WXAPI.wxarticles({
            page: page1,
            sessionId: sessionId,
            apiName: 'WX_GET_ARTICLE',
            appId: 'wxa192d06cd30b94b7'
        }).then(function(res) {
            if (sessionId != 123) {
                //登录后看到的数据

                if (res.data.ranks.length != 0) {
                    var ranks1 = that.data.ranks;
                    for (var i = 0; i < res.data.ranks.length; i++) {
                        ranks1.push(res.data.ranks[i]);
                    }
                    that.setData({
                        userCommentHidden: false,
                        ranks: ranks1,
                        isLoading: true,
                        hiddenNO: true,

                    })
          

                } else {
                    that.setData({
                        isLoading: false,
                        noMoreHidden: false,
                    })
                }
            } else {
                //没有登录看到的数据
                if (res.data.ranks.length != 0) {
                    var ranks1 = that.data.ranks;
                    for (var i = 0; i < res.data.ranks.length; i++) {
                        ranks1.push(res.data.ranks[i]);
                    }
                    that.setData({
                        userCommentHidden: false,
                        ranks: ranks1,
                        isLoading: true,
                        hiddenNO: true,
                    })
        

                } else {
                    that.setData({
                        isLoading: false,
                        noMoreHidden: false,
                    })
                }
            }          

          wx.stopPullDownRefresh();
        })
    },
    //我的评论展开和收起
    down: function() {
        var that = this;
        if (that.data.my_index1 == 0) {
            that.setData({
                my_index1: 9999,
                text: '点击收起'
            })
        } else {
            that.setData({
                my_index1: 0,
                text: '点击展开'
            })
        }
    },
    //点击跳转详情页面
    detailPage: function(e) {     
        var aid = e.currentTarget.dataset.aid;
        var sessionId = this.data.sessionId;
        var time=this.data.time;
        var topImg = this.data.topImg;
        wx.navigateTo({
            url: "../detail/detail?detail=" + aid + "|" + sessionId + "|" + time + "|" + topImg


        })
    },
    //分享跳转分享后页面
    sharePage: function(e) {
        var aid = e.currentTarget.dataset.aid;
        var sessionId = this.data.sessionId;
        var time = this.data.time;
        var topImg = this.data.topImg;
        var competitionName = this.data.competitionName;
        wx.navigateTo({
            url: "../share/share?detail=" + aid + "|" + sessionId + "|" + time + "|" + topImg + "|" + competitionName

        })
    },
    //说明
    introFun: function() {
        wx.navigateTo({
            url: "../explain/explain"

        })
    },
    bindGetUserInfo: function(e) {
        var that = this;
        if (e.detail.userInfo) {
            //用户按了允许授权按钮         
            that.onLoad();
        } else {
            //用户按了拒绝按钮
        }
    },
    //我的评论点赞
    myZan: function(e) {
        var that = this;
        var ourselfs = that.data.ourselfs;
        var method = 1;
        var sessionId = that.data.sessionId;
        var aid = e.currentTarget.dataset.aid;
        var num = e.currentTarget.dataset.thumbs;
        console.log(num);
        var isthumbs = e.currentTarget.dataset.isthumbs;
          WXAPI.zan({
            sessionId: sessionId,
            method: method,
            aid: aid,
            apiName: 'WX_ARTICLES_THUMBS',
            appId: 'wxa192d06cd30b94b7',

          }).then(function (res) {
            if (res.code == 4000) {
              var index = -1;
              for (var i = 0; i < ourselfs.length; i++) {
                if (ourselfs[i].art.aid == aid) {
                  index = i;
                }
              }
              if (index != -1) {
                that.setData({
                  [`ourselfs[${index}].art.isthumbs`]: true,
                  [`ourselfs[${index}].art.thumbs`]: num + 1
                })
              }
              wx.showToast({
                title: "点赞成功",
                icon: 'none',
                duration: 2000
              })
            } else {
              wx.showToast({
                title: res.msg,
                icon: 'none',
                duration: 2000
              })
            }
          })

  }, 
  userZan: function (e) {
    var that = this;
    var method = 1;
    var sessionId = that.data.sessionId;
    var aid = e.currentTarget.dataset.aid;
    var num = e.currentTarget.dataset.thumbs;
    var ranks = that.data.ranks;
    WXAPI.zan({
      sessionId: sessionId,
      method: method,
      aid: aid,
      apiName: 'WX_ARTICLES_THUMBS',
      appId: 'wxa192d06cd30b94b7'
    }).then(function (res) {
      console.log(res.data);
      if (res.code == 4000) {
        var index = -1; 
        for (var i = 0; i < ranks.length; i++) {
          if (ranks[i].art.aid == aid) {
            index = i;
          }
        }
        if(index != -1){
          that.setData({
            [`ranks[${index}].art.isthumbs`]: true,
            [`ranks[${index}].art.thumbs`]: num+1
          })
        }
        wx.showToast({
          title: '点赞成功',
          icon: 'none',
          duration: 2000
        })
      } else {
        wx.showToast({
          title: res.msg,
          icon: 'none',
          duration: 2000
        })
      }
    })
  },
    //中奖结果
    resultFun: function() {
        var that = this;
        var sessionId = that.data.sessionId;
        WXAPI.result(sessionId).then(function(res) {         
            if (res.code == 4000) {
                that.setData({
                    findPrize: false,
                    resultRanks: res.data.ranks
                })
                if (res.data.myself.code == 0) {
                    //未中奖
                    that.setData({
                        resulTip: '很遗憾，您没有中奖',
                        getPrize: true,
                    })

                } else if (res.data.myself.code == 1) {
                    //已经中奖
                    that.setData({
                        resulTip: '恭喜您中奖啦!',
                        getPrize: false,
                        introAid: res.data.myself.aid
                    })

                } else if (res.data.myself.code == 2) {
                    //已经领过奖
                    that.setData({
                        resulTip: '您已经领过奖了!',
                        getPrize: true
                    })

                }


            }


        })
    },
    //关闭
    close: function() {
        this.setData({
            prizeBox: true,
            commonScreen:true,
        })
    },
    //查看中奖名单
    findPrize: function() {     
       this.setData({
           prizeBox:false,
           commonScreen: false,
       })                 
    },
    //去领奖
    getPrize: function() {
        var aid = this.data.introAid;
        wx.navigateTo({
            url: "../address/address?aid=" + aid

        })
    },
    joinFun: function(e) {
        var that = this;
        wx.getSetting({
            success: function(res) {
                if (res.authSetting['scope.userInfo']) {
                    wx.navigateTo({
                        url: "../post/post"
                    })

                } else {
                    wx.navigateTo({
                        url: "../authorize/index"
                    })

                }
            }
        })
    },
    //
    imgFun:function(){
        var that=this;
        if(that.data.topImg  == ''){
          WXAPI.explain().then(function (res) {  
              console.log(res)
              var startdate = res.data.startdate;
              var enddate = res.data.enddate;
              startdate = startdate.split(' ');
              startdate = startdate[0];
              startdate = startdate.replace(/-/g, ".");
              enddate = enddate.split(' ');
              enddate = enddate[0];
              enddate = enddate.replace(/-/g, ".");
              that.setData({
                  prizeName: res.data.prizeName,
                  topImg: 'https://wxapp.zyzsbj.cn/wxappservice/api/file/get/image/'+res.data.logoImg,
                  time: startdate +"-"+enddate,
                  findUrl: res.data.clickUrl,
                  competitionName:res.data.name,
              })
          })
        }



    },
    imgYu: function (event) {
        var src = event.currentTarget.dataset.src;//获取data-src
        var imgList = event.currentTarget.dataset.list;//获取data-list
        //图片预览
        wx.previewImage({
            current: src, // 当前显示图片的http链接
            urls: imgList // 需要预览的图片http链接列表
        })
    },
    userimgYu: function (event) {
        var src = event.currentTarget.dataset.src;//获取data-src
        var imgList = event.currentTarget.dataset.list;//获取data-list
        //图片预览
        wx.previewImage({
            current: src, // 当前显示图片的http链接
            urls: imgList // 需要预览的图片http链接列表
        })
    },
    //判断授权
  judgeSQ: function () {
    var that = this;
    that.setData({
      sessionId: wx.getStorageSync('token')
    })
    if (that.data.ranks.length==0){
       that.dataList();
    }
    that.prizeList();  
  }, 
    /**
     * 
    judgeSQ:function(){
        var that = this;
        wx.getSetting({
            success: function (res) {
                if (res.authSetting['scope.userInfo']) {         
                    console.log('授权');                 
                    that.setData({
                        sessionId: wx.getStorageSync('sessionId')
                    })   
                    that.dataList();
                    that.prizeList();   
                                      
              } else {                   
                    that.dataList();
                    console.log('未授权');
                    that.prizeList(); 
                }
            }
        })
    },
    */
    prizeList:function(){
        var that = this;
        var sessionId = that.data.sessionId;
        WXAPI.result(sessionId).then(function (res) {
            if (res.code == 4000) {
                that.setData({               
                    resultRanks: res.data.ranks,
                    findPrize: false,
                })
                if (res.data.myself.code == 0) {
                    //未中奖
                    that.setData({
                        resulTip: '很遗憾，您没有中奖',
                        getPrize: true,
                    })

                } else if (res.data.myself.code == 1) {
                    //已经中奖
                    that.setData({
                        resulTip: '恭喜您中奖啦!',
                        getPrize: false,
                        introAid: res.data.myself.aid
                    })

                } else if (res.data.myself.code == 2) {
                    //已经领过奖
                    that.setData({
                        resulTip: '您已经领过奖了!',
                        getPrize: true
                    })

                }


            }


        })

    },


   

})