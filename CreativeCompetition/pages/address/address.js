//index.js
//获取应用实例
var app = getApp()
var area = require('../../utils/area.js')
const WXAPI = require('../../wxapi/main')

var areaInfo = [];//所有省市区县数据

var provinces = [];//省

var citys = [];//城市

var countys = [];//区县

var index = [0, 0, 0];

var cellId;

var t = 0;
var show = false;
var moveY = 200;

Page({
  data: {
    show: show,
    provinces: provinces,
    citys: citys,
    countys: countys,
    value: [0, 0, 0],
    name:'',
    phone:'',
    address:'',
    aid:''
  },
  //滑动事件
  bindChange: function (e) {
    var val = e.detail.value
    //判断滑动的是第几个column
    //若省份column做了滑动则定位到地级市和区县第一位
    if (index[0] != val[0]) {
      val[1] = 0;
      val[2] = 0;
      getCityArr(val[0], this);//获取地级市数据
      getCountyInfo(val[0], val[1], this);//获取区县数据
    } else {    //若省份column未做滑动，地级市做了滑动则定位区县第一位
      if (index[1] != val[1]) {
        val[2] = 0;
        getCountyInfo(val[0], val[1], this);//获取区县数据
      }
    }
    index = val;

    //更新数据
    this.setData({
      value: [val[0], val[1], val[2]],
      province: provinces[val[0]].name,
      city: citys[val[1]].name,
      county: countys[val[2]].name
    })

  },
  onLoad: function (options) {
      var aid = options.aid;
      this.setData({
          aid: aid
      })
    cellId = options.cellId;
    var that = this;
    var date = new Date()
    //获取省市区县数据
    area.getAreaInfo(function (arr) {
      areaInfo = arr;
      //获取省份数据
      getProvinceData(that);
    });

  },
  // ------------------- 分割线 --------------------
  onReady: function () {
    this.animation = wx.createAnimation({
      transformOrigin: "50% 50%",
      duration: 0,
      timingFunction: "ease",
      delay: 0
    }
    )
    this.animation.translateY(200 + 'vh').step();
    this.setData({
      animation: this.animation.export(),
      show: show
    })
  },
  //移动按钮点击事件
  translate: function (e) {
    if (t == 0) {
      moveY = 0;
      show = false;
      t = 1;
    } else {
      moveY = 200;
      show = true;
      t = 0;
    }
    // this.animation.translate(arr[0], arr[1]).step();
    animationEvents(this, moveY, show);

  },
  //隐藏弹窗浮层
  hiddenFloatView(e) {
    moveY = 200;
    show = true;
    t = 0;
    animationEvents(this, moveY, show);

  },
  //页面滑至底部事件
  onReachBottom: function () {
    // Do something when page reach bottom.
  },
  tiaozhuan() {
    wx.navigateTo({
      url: '../../pages/modelTest/modelTest',
    })
  },
  // 保存地址
  formSubmit(e) {
    const name=this.data.name;
    const phone = this.data.phone;
    const city = this.data.city;
    const province = this.data.province;
    const district = this.data.county;
    const location = this.data.address;
    const address = province + city + district + location;
    let sessionId = wx.getStorageSync('token');
    const aid=this.data.aid;
    var reg1 = /^1[34578]\d{9}$/;
    var reg2 = /(?=.*?[\u4E00-\u9FA5])/;
    if (name == "") {
      wx.showToast({
        title: '姓名不能为空',
        icon: 'none',
        duration: 2000
      })  
     
    } else if (!reg1.test(phone)) {
      wx.showToast({
        title: '请输入正确的手机号',
        icon: 'none',
        duration: 2000
      })
    } else if (province=="请选择") {
      wx.showToast({
        title: '请选择省市县区',
        icon: 'none',
        duration: 2000
      })
    } else if (!reg2.test(location)) {
      wx.showToast({
        title: '详细地址不能全为数字或者字母或者为空',
        icon: 'none',
        duration: 2000
      })  

    } else {
      WXAPI.save({
        sessionId: sessionId,
        appId: 'wxa192d06cd30b94b7',
        apiName: 'ADD_USER_ADDRESS',
        aid: aid,
        name: name,
        phone: phone,
        address: address,
        city: city,
        district: district,
        province: province,
        isDefault:false
      }).then(function (res) {
        if (res.data =='OK'){
           wx.navigateTo({
          url: "../competition/competition"
        })
        }
       
      })

      
    }
     
    
  },
  formName: function (e) {
    this.setData({
      name: e.detail.value
    })
  },
  formPhone: function (e) {
    this.setData({
      phone: e.detail.value
    })
  },
  formAddress: function (e) {
    this.setData({
      address: e.detail.value
    })
  },

})

//动画事件
function animationEvents(that, moveY, show) {
  that.animation = wx.createAnimation({
    transformOrigin: "50% 50%",
    duration: 400,
    timingFunction: "ease",
    delay: 0
  }
  )
  that.animation.translateY(moveY + 'vh').step()

  that.setData({
    animation: that.animation.export(),
    show: show
  })

}

// ---------------- 分割线 ---------------- 

//获取省份数据
function getProvinceData(that) {
  var s;
  provinces = [];
  var num = 0;
  for (var i = 0; i < areaInfo.length; i++) {
    s = areaInfo[i];
    if (s.di == "00" && s.xian == "00") {
      provinces[num] = s;
      num++;
    }
  }
  that.setData({
    provinces: provinces
  })

  //初始化调一次
  getCityArr(0, that);
  getCountyInfo(0, 0, that);
  that.setData({
    province: "请选择",
    city: "",
    county: "",
  })

}

// 获取地级市数据
function getCityArr(count, that) {
  var c;
  citys = [];
  var num = 0;
  for (var i = 0; i < areaInfo.length; i++) {
    c = areaInfo[i];
    if (c.xian == "00" && c.sheng == provinces[count].sheng && c.di != "00") {
      citys[num] = c;
      num++;
    }
  }
  if (citys.length == 0) {
    citys[0] = { name: '' };
  }

  that.setData({
    city: "",
    citys: citys,
    value: [count, 0, 0]
  })
}

// 获取区县数据
function getCountyInfo(column0, column1, that) {
  var c;
  countys = [];
  var num = 0;
  for (var i = 0; i < areaInfo.length; i++) {
    c = areaInfo[i];
    if (c.xian != "00" && c.sheng == provinces[column0].sheng && c.di == citys[column1].di) {
      countys[num] = c;
      num++;
    }
  }
  if (countys.length == 0) {
    countys[0] = { name: '' };
  }
  that.setData({
    county: "",
    countys: countys,
    value: [column0, column1, 0]
  })
}
