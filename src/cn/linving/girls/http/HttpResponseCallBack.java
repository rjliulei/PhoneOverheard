package cn.linving.girls.http;

public interface HttpResponseCallBack {
	/**
	 * 
	 * @param url
	 *            请求地址
	 * @param result
	 *            请求结果
	 */
	public void onSuccess(String url, String result);

	/**
	 * 
	 * @param httpResponseCode
	 *            http 返回码
	 * @param errCode
	 *            错误码
	 * @param err
	 *            错误详情
	 * 
	 */

	public void onFailure(int httpResponseCode, int errCode, String err);

}
