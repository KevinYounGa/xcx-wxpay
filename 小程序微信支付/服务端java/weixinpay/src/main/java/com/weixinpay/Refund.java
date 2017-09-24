......
@RequestMapping(params = "refund", method = RequestMethod.POST)
	@Transactional
	public @ResponseBody Map<String, Object> refund(String id,String user,HttpServletRequest request) {
		Map<String,Object> result = new HashMap<String,Object>();
		String currTime = PayUtils.getCurrTime();
		String strTime = currTime.substring(8, currTime.length());
		String strRandom = PayUtils.buildRandom(4) + "";
		String nonceStr = strTime + strRandom;
		String outRefundNo = "wx@re@"+PayUtils.getTimeStamp();
		String outTradeNo = "";
		String transactionId = "";

		String unionId = request.getParameter("openid");
		String orderId = request.getParameter("orderId");
		String appid = ResourceUtil.getConfigByName("wx.application.appid");
		String mchid = ResourceUtil.getConfigByName("wx.application.mch_id");
		String key =  ResourceUtil.getConfigByName("wx.application.mch_key");
		WxOrderInfoEntity orderInfoEntity = new WxOrderInfoEntity();
		if(StringUtil.isNotEmpty(orderId)){
			orderInfoEntity = orderInfoService.getEntity(WxOrderInfoEntity.class,orderId);
			//ProfPayLog payLog = wxappOrderService.findByPayLogId(Long.valueOf(id));
			int total_fee = orderInfoEntity.getTotalFee();
			DecimalFormat df = new DecimalFormat("0.00");
			//String fee = String.valueOf(df.format((float)total_fee/100));
			String fee = String.valueOf(total_fee);
			SortedMap<String, String> packageParams = new TreeMap<String, String>();
			packageParams.put("appid", appid);
			packageParams.put("mch_id", mchid);//微信支付分配的商户号
			packageParams.put("nonce_str", nonceStr);//随机字符串，不长于32位
			packageParams.put("op_user_id", mchid);//操作员帐号, 默认为商户号
			//out_refund_no只能含有数字、字母和字符_-|*@
			packageParams.put("out_refund_no", outRefundNo);//商户系统内部的退款单号，商户系统内部唯一，同一退款单号多次请求只退一笔
			packageParams.put("out_trade_no", orderInfoEntity.getOutTradeNo());//商户侧传给微信的订单号32位
			packageParams.put("refund_fee", fee);
			packageParams.put("total_fee", fee);
			packageParams.put("transaction_id", transactionId);//微信生成的订单号，在支付通知中有返回
			String sign = PayUtils.createSign(packageParams,key);

			String refundUrl = "https://api.mch.weixin.qq.com/secapi/pay/refund";
			String xmlParam="<xml>"+
					"<appid>"+appid+"</appid>"+
					"<mch_id>"+mchid+"</mch_id>"+
					"<nonce_str>"+nonceStr+"</nonce_str>"+
					"<op_user_id>"+mchid+"</op_user_id>"+
					"<out_refund_no>"+outRefundNo+"</out_refund_no>"+
					"<out_trade_no>"+orderInfoEntity.getOutTradeNo()+"</out_trade_no>"+
					"<refund_fee>"+fee+"</refund_fee>"+
					"<total_fee>"+fee+"</total_fee>"+
					"<transaction_id>"+transactionId+"</transaction_id>"+
					"<sign>"+sign+"</sign>"+
					"</xml>";
			logger.info("---------xml返回:"+xmlParam);
			String resultStr = PayUtils.post(refundUrl, xmlParam);
			logger.info("---------退款返回:"+resultStr);
			//解析结果
			try {
				Map map =  PayUtils.doXMLParse(resultStr);
				String returnCode = map.get("return_code").toString();
				if(returnCode.equals("SUCCESS")){
					String resultCode = map.get("result_code").toString();
					if(resultCode.equals("SUCCESS")){
						//保存退款记录
						WxRefundInfoEntity refundInfoEntity = new WxRefundInfoEntity();
						refundInfoEntity.setCreateDate(new Date());
						refundInfoEntity.setAppid(appid);
						refundInfoEntity.setMchId(mchid);
						refundInfoEntity.setOpUserId(mchid);
						refundInfoEntity.setNonceStr(nonceStr);
						refundInfoEntity.setSign(sign);
						refundInfoEntity.setOutRefundNo(outRefundNo);
						refundInfoEntity.setOutTradeNo(orderInfoEntity.getOutTradeNo());
						refundInfoEntity.setTotalFee(total_fee);
						refundInfoEntity.setRefundFee(total_fee);
						refundInfoEntity.setUnionid(unionId);
						wxRefundInfoService.save(refundInfoEntity);
						/*ProfPayLog profPayLog = new ProfPayLog();
						profPayLog.setCreatedAt(new Date());
						profPayLog.setSource(payLog.getSource());
						profPayLog.setTotalFee(payLog.getTotalFee());
						profPayLog.setTradeNo(payLog.getTradeNo());
						profPayLog.setTransactionId(map.get("refund_id").toString());
						profPayLog.setUserId(user);
						profPayLog.setType(ProfPayLog.Type.Refund);
						profPayLog = wxappOrderService.save(profPayLog);*/
						result.put("status", "success");
					}else{
						result.put("status", "fail");
					}
				}else{
					result.put("status", "fail");
				}
			} catch (Exception e) {
				e.printStackTrace();
				result.put("status", "fail");
			}
		}
		return result;
	}
  ....
