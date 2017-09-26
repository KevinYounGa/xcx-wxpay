
........................
/**
	 * 发送支付成功后的模板消息
	 * */
	@RequestMapping(params = "sendZFTemplateMsg",method = RequestMethod.POST)
	@ResponseBody
	public void sendZFTemplateMsg(HttpServletRequest request){
		String url = "https://api.weixin.qq.com/cgi-bin/message/wxopen/template/send";
		String access_token = "";
    //取access_token
		List<WxAccessTokenEntity> accessTokenEntityList = accessTokenService.getList(WxAccessTokenEntity.class);
    if(accessTokenEntityList.size() > 0) {
			WxAccessTokenEntity atEntity = accessTokenEntityList.get(0);
			access_token = atEntity.getAccessToken();
		}
    
		url = url + "?access_token=" + access_token;
		String touser = request.getParameter("openid");
    //选取的模板消息id，进入小程序公众后台可得
		String template_id = "SoIUJGDHDb_4D4Lu3sb9b1TdIw-0MF3UJHYGvVzcC4Y";
		//支付时下单而得的prepay_id
		String form_id = request.getParameter("prepay_id");
		String body = request.getParameter("body");
		String scene = request.getParameter("scene");
		Date curDate = new Date();
		String curDateStr = DateUtils.date2Str(curDate,DateUtils.time_sdf);
		StringBuffer buffer = new StringBuffer();
		//按照官方api的要求提供params
    buffer.append("{");
		buffer.append(String.format("\"touser\":\"%s\"", touser)).append(",");
		buffer.append(String.format("\"template_id\":\"%s\"", template_id)).append(",");
		buffer.append(String.format("\"page\":\"%s\"", "pages/course/course")).append(",");
		buffer.append(String.format("\"form_id\":\"%s\"", form_id)).append(",");
		buffer.append("\"data\":{");
		buffer.append(String.format("\"%s\": {\"value\":\"%s\",\"color\":\"%s\"},","keyword1", curDateStr, "#173177"));
		buffer.append(String.format("\"%s\": {\"value\":\"%s\",\"color\":\"%s\"},","keyword2", body, "#173177"));
		buffer.append(String.format("\"%s\": {\"value\":\"%s\",\"color\":\"%s\"}","keyword3", gymName, "#173177"));
		buffer.append("}");
		buffer.append("}");
		String params = "";
		try {
			params = new String(buffer.toString().getBytes("UTF-8"));
			System.out.println("utf-8 编码：" + params) ;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String sr = HttpRequest.sendPost(url,params);
		System.out.println("模板消息返回——" + sr);
	}
  ....................
