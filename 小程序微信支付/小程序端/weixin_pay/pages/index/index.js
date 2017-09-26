//index.js
//获取应用实例
var app = getApp()
Page({
  data: {
    motto: 'Hello World',
    userInfo: {}
  },
  onLoad: function () {
    console.log('onLoad')
    
  },
  //
  payoff: function(e){
    var that = this;
    wx.login({
      success: function(res) {
        that.getOpenId(res.code);
      }
    });
    
  },
  //获取openid
  getOpenId: function(code){
    var that = this;
    wx.request({ 
        url: 'getApp().globalData.serverUrl + '/wxController.do?GetOpenId', 
        method: 'POST',
        header: {
           'content-type': 'application/x-www-form-urlencoded'
        },
        data: {'code':code},
        success: function(res) {
           var openId = res.data.openid;
           wx.setStorageSync('openid', openId);
           that.xiadan(openId);
        }
    })
  },
  //下单
  xiadan: function(openId){
    var that = this;
    wx.request({
        url: 'getApp().globalData.serverUrl + '/wxController.do?xiadan', 
        method: 'POST',
        header: {
           'content-type': 'application/x-www-form-urlencoded'
        },
        data: {'openid':openId},
        success: function(res) {
           var prepay_id = res.data.prepay_id;
           console.log("统一下单返回 prepay_id:"+prepay_id);
           that.sign(prepay_id);
        }
    })
  },
  //签名
  sign: function(prepay_id){
    var that = this;
    wx.request({
        url: 'getApp().globalData.serverUrl + '/wxController.do?sign', 
        method: 'POST',
        header: {
           'content-type': 'application/x-www-form-urlencoded'
        },
        data: {'repay_id':prepay_id},
        success: function(res) {
           that.requestPayment(res.data);

        }
    })
  },
  //申请支付
  requestPayment: function(obj){
    wx.requestPayment({
      'timeStamp': obj.timeStamp,
      'nonceStr': obj.nonceStr,
      'package': obj.package,
      'signType': obj.signType,
      'paySign': obj.paySign,
      'success':function(res){
        
         //发送支付成功的模板消息
          wx.request({
              url: getApp().globalData.serverUrl + '/wxController.do?sendTemplateMsg',
              method: 'POST',
                header: {
                  'content-type': 'application/x-www-form-urlencoded'
               },
              data: {
                'openid': wx.getStorageSync('openId'),
                'prepay_id': prepay_id,
                'body': that.data.body,
                'scene': scene
                },
                success: function (res) {
                  console.log(res);
                }
            })
      },
      'fail':function(res){
      }
    })
  },
  //退款
  refund: function(){
    var that = this;
    wx.request({ 
        url: 'getApp().globalData.serverUrl + '/wxController.do?refund', 
        method: 'POST',
        header: {
           'content-type': 'application/x-www-form-urlencoded'
        },
        data: {
         'unionid': wx.getStorageSync("openid"),
          //需退款的订单id
         'orderId':orderid
        },
        success: function(res) {
          
        }
    })
  },
})
