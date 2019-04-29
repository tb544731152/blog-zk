const WXAPI = require('../../wxapi/main')
var app = getApp()
Page({
    data: {
        uploaderList: [],
        uploaderNum: 0,
        showUpload: true,
        dataValue: [],
        content: '',
        max: 500,
        current: 0,
        forbid: false,
        one: true,

    },
  /**
      * 生命周期函数--监听页面加载
      */
  onLoad: function (options) {
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
  },
    loadimg: function(e) {
        var that = this;
        var dataValue = that.data.dataValue;
        var imgs = dataValue.join(',');
        const content = this.data.content;
        const formId = e.detail.formId;
        let sessionId = wx.getStorageSync('token');
        var one = that.data.one;
        var images = that.data.uploaderList;
        if (content != "" && content != null) {
            if (one == true) {
                WXAPI.issue({
                    sessionId: sessionId,
                    appId: 'wxa192d06cd30b94b7',
                    apiName: 'WX_ARTICLE_SAVE',
                    title: '',
                    content: content,
                    imgs: imgs,
                    form_id: formId
                }).then(function(res) {
                    if (res.data == true) {
                        wx.showToast({
                            title: '发布成功',
                            icon: 'success',
                            duration: 2000
                        })
                        setTimeout(function() {
                            wx.navigateTo({
                                url: "../competition/competition"
                            })
                        }, 2000);

                    }
                })

                that.setData({
                    one: false,
                })



            }
        } else {
            wx.showToast({
                title: '文字不能为空',
                icon: 'none',
                duration: 2000
            })

        }
    },
    bindBlur: function(e) {
        var value = e.detail.value;
        var len = parseInt(value.length);
        if (len > this.data.max) {
            return
        }
        this.setData({
            content: e.detail.value,
            current: len
        })

    },
    clearImg: function (e) {
        var nowList = [];//新数据
        var uploaderList = this.data.uploaderList;//原数据
        var dataList = [];
        var dataValue = this.data.dataValue;

        for (let i = 0; i < uploaderList.length; i++) {
            if (i == e.currentTarget.dataset.index) {
                continue;
            } else {
                nowList.push(uploaderList[i])
            }
        }

        for (let i = 0; i < dataValue.length; i++) {
            if (i == e.currentTarget.dataset.index) {
                continue;
            } else {
                dataList.push(dataValue[i])
            }
        }
        this.setData({
            uploaderNum: this.data.uploaderNum - 1,
            uploaderList: nowList,
            dataValue: dataList,
            showUpload: true
        })
    },

    //展示图片
    showImg: function(e) {
        var that = this;
        wx.previewImage({
            urls: that.data.uploaderList,
            current: that.data.uploaderList[e.currentTarget.dataset.index]
        })
    },
    //上传图片
    upload: function (e) {
        var that = this;
        wx.chooseImage({
            count: 3 - that.data.uploaderNum, // 默认9
            sizeType: ['original', 'compressed'], // 可以指定是原图还是压缩图，默认二者都有
            sourceType: ['album', 'camera'], // 可以指定来源是相册还是相机，默认二者都有
            success: function (res) {
                // 返回选定照片的本地文件路径列表，tempFilePath可以作为img标签的src属性显示图片
                let tempFilePaths = res.tempFilePaths;
                for (var i = 0, h = tempFilePaths.length; i < h; i++) {
                    wx.uploadFile({
                        url: 'https://wxapp.zyzsbj.cn/wxappservice/api/file/upload/image',
                        filePath: tempFilePaths[i],
                        name: 'file',
                        header: {},
                        formData: {
                            appId: 'wxa192d06cd30b94b7',
                            apiName: 'UPLOAD_IMAGE'
                        },
                        success(res) {
                            const data = JSON.parse(res.data);
                            let dataValue = that.data.dataValue;
                            const imgData = data.data;
                            dataValue.push(imgData);


                        }
                    })

                }

                let uploaderList = that.data.uploaderList.concat(tempFilePaths);

                if (uploaderList.length == 3) {
                    that.setData({
                        showUpload: false
                    })
                }
                that.setData({
                    uploaderList: uploaderList,
                    uploaderNum: uploaderList.length,
                })
            }
        })
  },
  goLoginPageTimeOut: function () {
    // wx.removeStorageSync('token')
    setTimeout(function () {
      wx.navigateTo({
        url: "/pages/authorize/index"
      })
    }, 1000)
  },


    imgfun: function() {
        var that = this;
        var images = that.data.uploaderList;
        for (var i = 0, h = images.length; i < h; i++) {
            wx.uploadFile({
                url: 'https://wxapp.zyzsbj.cn/wxappservice/api/file/upload/image',
                filePath: images[i],
                name: 'file',
                header: {},
                formData: {
                    appId: 'wxa192d06cd30b94b7',
                    apiName: 'UPLOAD_IMAGE'
                },
                success(res) {
                    const data = JSON.parse(res.data);
                    let dataValue = that.data.dataValue;
                    const imgData = data.data;
                    dataValue.push(imgData);
                    that.setData({
                        dataValue: dataValue
                    })

                }
            })
        }
    }
})