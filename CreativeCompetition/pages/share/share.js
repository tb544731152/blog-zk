// pages/share/share.js
const WXAPI = require('../../wxapi/main')
Page({

    /**
     * 页面的初始数据
     */
    data: {
        time: '2019年1月20日-5月20日',
        //  systemInfo: {}, 
        aid: '',
        headimg: '',
        nickname: '',
        createTime: '',
        content: '',
        rank: '',
        thumbs: '',
        zan: '点赞',
        isthumbs: '',
        aid: '',
        imgs: '',
        sessionId: '',
        aid1:'',
        topImg:'',
        competitionName:'',

    },

    /**
     * 生命周期函数--监听页面加载
     */
    onLoad: function (options) {
        var that = this;
        var str = options.detail;
        var detail = str.split("|");
        this.setData({
            aid1: detail[0],
            sessionId: detail[1],
            time: detail[2],
            topImg: detail[3],
            competitionName:detail[4],


        })
        var aid1 = that.data.aid1;
        var sessionId = that.data.sessionId;
        WXAPI.getwxarticle({
            sessionId: sessionId,
            aid: aid1,
            apiName: 'WX_GET_ARTICLE',
            appId: 'wxa192d06cd30b94b7'
        }).then(function (res) {
            var imgs = res.data.imgs;
            if (imgs != null) {
                var imgs1 = imgs.split(",");
                that.setData({
                    imgs: imgs1,
                })

            }

            that.setData({
                headimg: res.data.art.author.headimg,
                nickname: res.data.art.author.nickname,
                createTime: res.data.art.createTime,
                content: res.data.art.content,
                rank: res.data.art.rank,
                thumbs: res.data.art.thumbs,
                isthumbs: res.data.art.isthumbs,
                aid: res.data.art.aid,
                imgs: res.data.imgsList
            })



        })




    },

    /**
     * 生命周期函数--监听页面初次渲染完成
     */
    onReady: function () {

    },

    /**
     * 生命周期函数--监听页面显示
     */
    onShow: function () {

    },

    /**
     * 生命周期函数--监听页面隐藏
     */
    onHide: function () {

    },

    /**
     * 生命周期函数--监听页面卸载
     */
    onUnload: function () {

    },

    /**
     * 页面相关事件处理函数--监听用户下拉动作
     */
    onPullDownRefresh: function () {

    },

    /**
     * 页面上拉触底事件的处理函数
     */
    onReachBottom: function () {

    },

    /**
     * 用户点击右上角分享
     */
    onShareAppMessage: function () {
        return {

            title: '我正在参加创意大赛，集赞有奖！帮我点个赞。。',


            //  path: '/page/user?id=123' // 路径，传递参数到指定页面。



        }
    },

    //点赞
    zan: function () {
        var method = 1;
        var aid = this.data.aid;
        var that=this;
        var sessionId = that.data.sessionId;
        wx.getSetting({
            success: function (res) {
                if (res.authSetting['scope.userInfo']) {
                    if (that.data.isthumbs == false) {
                        WXAPI.zan({
                            sessionId: sessionId,
                            method: method,
                            aid: aid,
                            apiName: 'WX_ARTICLES_THUMBS',
                            appId: 'wxa192d06cd30b94b7',

                        }).then(function (res) {
                            if (res.code == 4000) {
                                var thumbs = that.data.thumbs + 1;
                                that.setData({
                                    thumbs: thumbs
                                })
                            } else if (res.code == 6001) {
                                wx.showToast({
                                    title: res.msg,
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
                    } else {
                        wx.showToast({
                            title: '已经点过了',
                            icon: 'none',
                            duration: 2000
                        })
                    }


                } else {
                    wx.navigateTo({
                        url: "../authorize/index"
                    })
                }
            }
        })
    },
    //说明
    introFun: function () {
        wx.navigateTo({
            url: "../explain/explain"

        })
    },
    //查看其它创意
    findOther: function () {
        wx.navigateTo({
            url: "../competition/competition"

        })
    },
    //查看放大预览
    imgYu: function (event) {
        var src = event.currentTarget.dataset.src;//获取data-src
        var imgList = event.currentTarget.dataset.list;//获取data-list
        //图片预览
        wx.previewImage({
            current: src, // 当前显示图片的http链接
            urls: imgList // 需要预览的图片http链接列表
        })
    }
})