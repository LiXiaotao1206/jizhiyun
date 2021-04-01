package com.gizwits.opensource.appkit.ControlModule;

import java.text.DecimalFormat;
import java.util.concurrent.ConcurrentHashMap;

import com.gizwits.gizwifisdk.api.GizWifiDevice;
import com.gizwits.gizwifisdk.enumration.GizWifiDeviceNetStatus;
import com.gizwits.gizwifisdk.enumration.GizWifiErrorCode;
import com.gizwits.gizwifisdk.listener.GizWifiDeviceListener;
import com.gizwits.opensource.appkit.CommonModule.GosBaseActivity;
import com.gizwits.opensource.appkit.utils.HexStrUtils;

import android.util.Log;
import android.content.Context;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

public class GosControlModuleBaseActivity extends GosBaseActivity {

	/*
	 * ===========================================================
	 * 以下key值对应开发者在云端定义的数据点标识名
	 * ===========================================================
	 */
	// 数据点"LLBJ"对应的标识名
	protected static final String KEY_LLBJ = "LLBJ";
	// 数据点"锅炉给水-PH"对应的标识名
	protected static final String KEY_GS_PH = "GS_PH";
	// 数据点"锅炉给水-CC"对应的标识名
	protected static final String KEY_GS_CC = "GS_CC";
	// 数据点"锅炉给水-O2"对应的标识名
	protected static final String KEY_GS_O2 = "GS_O2";
	// 数据点"炉水-SC"对应的标识名
	protected static final String KEY_LS_SC = "LS_SC";
	// 数据点"炉水-pH"对应的标识名
	protected static final String KEY_LS_PH = "LS_pH";
	// 数据点"炉水-PO4"对应的标识名
	protected static final String KEY_LS_PO4 = "LS_PO4";
	// 数据点"饱和蒸汽-Na"对应的标识名
	protected static final String KEY_BH_NA = "BH_Na";
	// 数据点"饱和蒸汽-CC"对应的标识名
	protected static final String KEY_BH_CC = "BH_CC";
	// 数据点"过热蒸汽-CC"对应的标识名
	protected static final String KEY_GR_CC = "GR_CC";
	// 数据点"过热蒸汽-Na"对应的标识名
	protected static final String KEY_GR_NA = "GR_Na";
	// 数据点"过热蒸汽-SiO2"对应的标识名
	protected static final String KEY_GR_SIO2 = "GR_SiO2";
	// 数据点"汽机凝结水-CC"对应的标识名
	protected static final String KEY_QJ_CC = "QJ_CC";
	// 数据点"汽机凝结水-O2"对应的标识名
	protected static final String KEY_QJ_O2 = "QJ_O2";
	// 数据点"Q"对应的标识名
	protected static final String KEY_Q = "Q";

	/*
	 * ===========================================================
	 * 以下数值对应开发者在云端定义的可写数值型数据点增量值、数据点定义的分辨率、seekbar滚动条补偿值
	 * _ADDITION:数据点增量值
	 * _RATIO:数据点定义的分辨率
	 * _OFFSET:seekbar滚动条补偿值
	 * APP与设备定义的协议公式为：y（APP接收的值）=x（设备上报的值）* RATIO（分辨率）+ADDITION（增量值）
	 * 由于安卓的原生seekbar无法设置最小值，因此代码中增加了一个补偿量OFFSET
	 * 实际上公式中的：x（设备上报的值）=seekbar的值+补偿值
	 * ===========================================================
	 */

	/*
	 * ===========================================================
	 * 以下变量对应设备上报类型为布尔、数值、扩展数据点的数据存储
	 * ===========================================================
	 */
	// 数据点"LLBJ"对应的存储数据
	protected static boolean data_LLBJ;
	// 数据点"锅炉给水-PH"对应的存储数据
	protected static double data_GS_PH;
	// 数据点"锅炉给水-CC"对应的存储数据
	protected static double data_GS_CC;
	// 数据点"锅炉给水-O2"对应的存储数据
	protected static double data_GS_O2;
	// 数据点"炉水-SC"对应的存储数据
	protected static double data_LS_SC;
	// 数据点"炉水-pH"对应的存储数据
	protected static double data_LS_pH;
	// 数据点"炉水-PO4"对应的存储数据
	protected static double data_LS_PO4;
	// 数据点"饱和蒸汽-Na"对应的存储数据
	protected static double data_BH_Na;
	// 数据点"饱和蒸汽-CC"对应的存储数据
	protected static double data_BH_CC;
	// 数据点"过热蒸汽-CC"对应的存储数据
	protected static double data_GR_CC;
	// 数据点"过热蒸汽-Na"对应的存储数据
	protected static double data_GR_Na;
	// 数据点"过热蒸汽-SiO2"对应的存储数据
	protected static double data_GR_SiO2;
	// 数据点"汽机凝结水-CC"对应的存储数据
	protected static double data_QJ_CC;
	// 数据点"汽机凝结水-O2"对应的存储数据
	protected static double data_QJ_O2;
	// 数据点"Q"对应的存储数据
	protected static double data_Q;

	/*
	 * ===========================================================
	 * 以下key值对应设备硬件信息各明细的名称，用与回调中提取硬件信息字段。
	 * ===========================================================
	 */
	protected static final String WIFI_HARDVER_KEY = "wifiHardVersion";
	protected static final String WIFI_SOFTVER_KEY = "wifiSoftVersion";
	protected static final String MCU_HARDVER_KEY = "mcuHardVersion";
	protected static final String MCU_SOFTVER_KEY = "mcuSoftVersion";
	protected static final String WIFI_FIRMWAREID_KEY = "wifiFirmwareId";
	protected static final String WIFI_FIRMWAREVER_KEY = "wifiFirmwareVer";
	protected static final String PRODUCT_KEY = "productKey";

	private Toast mToast;
	
	@SuppressWarnings("unchecked")
	protected void getDataFromReceiveDataMap(ConcurrentHashMap<String, Object> dataMap) {
		// 已定义的设备数据点，有布尔、数值和枚举型数据

		if (dataMap.get("data") != null) {
			ConcurrentHashMap<String, Object> map = (ConcurrentHashMap<String, Object>) dataMap.get("data");
			for (String dataKey : map.keySet()) {
				if (dataKey.equals(KEY_LLBJ)) {
					data_LLBJ = (Boolean) map.get(dataKey);			
				}
				if (dataKey.equals(KEY_GS_PH)) {
			
					if (map.get(dataKey) instanceof Integer) {
						data_GS_PH = (Integer) map.get(dataKey);
					} else {
						data_GS_PH = (Double) map.get(dataKey);
					}
				}
				if (dataKey.equals(KEY_GS_CC)) {
			
					if (map.get(dataKey) instanceof Integer) {
						data_GS_CC = (Integer) map.get(dataKey);
					} else {
						data_GS_CC = (Double) map.get(dataKey);
					}
				}
				if (dataKey.equals(KEY_GS_O2)) {
			
					if (map.get(dataKey) instanceof Integer) {
						data_GS_O2 = (Integer) map.get(dataKey);
					} else {
						data_GS_O2 = (Double) map.get(dataKey);
					}
				}
				if (dataKey.equals(KEY_LS_SC)) {
			
					if (map.get(dataKey) instanceof Integer) {
						data_LS_SC = (Integer) map.get(dataKey);
					} else {
						data_LS_SC = (Double) map.get(dataKey);
					}
				}
				if (dataKey.equals(KEY_LS_PH)) {
			
					if (map.get(dataKey) instanceof Integer) {
						data_LS_pH = (Integer) map.get(dataKey);
					} else {
						data_LS_pH = (Double) map.get(dataKey);
					}
				}
				if (dataKey.equals(KEY_LS_PO4)) {
			
					if (map.get(dataKey) instanceof Integer) {
						data_LS_PO4 = (Integer) map.get(dataKey);
					} else {
						data_LS_PO4 = (Double) map.get(dataKey);
					}
				}
				if (dataKey.equals(KEY_BH_NA)) {
			
					if (map.get(dataKey) instanceof Integer) {
						data_BH_Na = (Integer) map.get(dataKey);
					} else {
						data_BH_Na = (Double) map.get(dataKey);
					}
				}
				if (dataKey.equals(KEY_BH_CC)) {
			
					if (map.get(dataKey) instanceof Integer) {
						data_BH_CC = (Integer) map.get(dataKey);
					} else {
						data_BH_CC = (Double) map.get(dataKey);
					}
				}
				if (dataKey.equals(KEY_GR_CC)) {
			
					if (map.get(dataKey) instanceof Integer) {
						data_GR_CC = (Integer) map.get(dataKey);
					} else {
						data_GR_CC = (Double) map.get(dataKey);
					}
				}
				if (dataKey.equals(KEY_GR_NA)) {
			
					if (map.get(dataKey) instanceof Integer) {
						data_GR_Na = (Integer) map.get(dataKey);
					} else {
						data_GR_Na = (Double) map.get(dataKey);
					}
				}
				if (dataKey.equals(KEY_GR_SIO2)) {
			
					if (map.get(dataKey) instanceof Integer) {
						data_GR_SiO2 = (Integer) map.get(dataKey);
					} else {
						data_GR_SiO2 = (Double) map.get(dataKey);
					}
				}
				if (dataKey.equals(KEY_QJ_CC)) {
			
					if (map.get(dataKey) instanceof Integer) {
						data_QJ_CC = (Integer) map.get(dataKey);
					} else {
						data_QJ_CC = (Double) map.get(dataKey);
					}
				}
				if (dataKey.equals(KEY_QJ_O2)) {
			
					if (map.get(dataKey) instanceof Integer) {
						data_QJ_O2 = (Integer) map.get(dataKey);
					} else {
						data_QJ_O2 = (Double) map.get(dataKey);
					}
				}
				if (dataKey.equals(KEY_Q)) {
			
					if (map.get(dataKey) instanceof Integer) {
						data_Q = (Integer) map.get(dataKey);
					} else {
						data_Q = (Double) map.get(dataKey);
					}
				}
			}
		}

		StringBuilder sBuilder = new StringBuilder();

		// 已定义的设备报警数据点，设备发生报警后该字段有内容，没有发生报警则没内容
		if (dataMap.get("alerts") != null) {
			ConcurrentHashMap<String, Object> map = (ConcurrentHashMap<String, Object>) dataMap.get("alerts");
			for (String alertsKey : map.keySet()) {
				if ((Boolean) map.get(alertsKey)) {
					sBuilder.append("报警:" + alertsKey + "=true" + "\n");
				}
			}
		}

		// 已定义的设备故障数据点，设备发生故障后该字段有内容，没有发生故障则没内容
		if (dataMap.get("faults") != null) {
			ConcurrentHashMap<String, Object> map = (ConcurrentHashMap<String, Object>) dataMap.get("faults");
			for (String faultsKey : map.keySet()) {
				if ((Boolean) map.get(faultsKey)) {
					sBuilder.append("故障:" + faultsKey + "=true" + "\n");
				}
			}
		}

		if (sBuilder.length() > 0) {
			sBuilder.insert(0, "[设备故障或报警]\n");
			myToast(sBuilder.toString().trim());
		}

		// 透传数据，无数据点定义，适合开发者自行定义协议自行解析
		if (dataMap.get("binary") != null) {
			byte[] binary = (byte[]) dataMap.get("binary");
			Log.i("", "Binary data:" + HexStrUtils.bytesToHexString(binary));
		}
	}

	GizWifiDeviceListener gizWifiDeviceListener = new GizWifiDeviceListener() {

		/** 用于设备订阅 */
		public void didSetSubscribe(GizWifiErrorCode result, GizWifiDevice device, boolean isSubscribed) {
			GosControlModuleBaseActivity.this.didSetSubscribe(result, device, isSubscribed);
		};

		/** 用于获取设备状态 */
		public void didReceiveData(GizWifiErrorCode result, GizWifiDevice device,
				java.util.concurrent.ConcurrentHashMap<String, Object> dataMap, int sn) {
			GosControlModuleBaseActivity.this.didReceiveData(result, device, dataMap, sn);
		};

		/** 用于设备硬件信息 */
		public void didGetHardwareInfo(GizWifiErrorCode result, GizWifiDevice device,
				java.util.concurrent.ConcurrentHashMap<String, String> hardwareInfo) {
			GosControlModuleBaseActivity.this.didGetHardwareInfo(result, device, hardwareInfo);
		};

		/** 用于修改设备信息 */
		public void didSetCustomInfo(GizWifiErrorCode result, GizWifiDevice device) {
			GosControlModuleBaseActivity.this.didSetCustomInfo(result, device);
		};

		/** 用于设备状态变化 */
		public void didUpdateNetStatus(GizWifiDevice device, GizWifiDeviceNetStatus netStatus) {
			GosControlModuleBaseActivity.this.didUpdateNetStatus(device, netStatus);
		};

	};

	/**
	 * 设备订阅回调
	 * 
	 * @param result
	 *            错误码
	 * @param device
	 *            被订阅设备
	 * @param isSubscribed
	 *            订阅状态
	 */
	protected void didSetSubscribe(GizWifiErrorCode result, GizWifiDevice device, boolean isSubscribed) {
	}

	/**
	 * 设备状态回调
	 * 
	 * @param result
	 *            错误码
	 * @param device
	 *            当前设备
	 * @param dataMap
	 *            当前设备状态
	 * @param sn
	 *            命令序号
	 */
	protected void didReceiveData(GizWifiErrorCode result, GizWifiDevice device,
			java.util.concurrent.ConcurrentHashMap<String, Object> dataMap, int sn) {
	}

	/**
	 * 设备硬件信息回调
	 * 
	 * @param result
	 *            错误码
	 * @param device
	 *            当前设备
	 * @param hardwareInfo
	 *            当前设备硬件信息
	 */
	protected void didGetHardwareInfo(GizWifiErrorCode result, GizWifiDevice device,
			java.util.concurrent.ConcurrentHashMap<String, String> hardwareInfo) {
	}

	/**
	 * 修改设备信息回调
	 * 
	 * @param result
	 *            错误码
	 * @param device
	 *            当前设备
	 */
	protected void didSetCustomInfo(GizWifiErrorCode result, GizWifiDevice device) {
	}

	/**
	 * 设备状态变化回调
	 */
	protected void didUpdateNetStatus(GizWifiDevice device, GizWifiDeviceNetStatus netStatus) {
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			this.finish();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	public void myToast(String string) {
		if (mToast != null) {
			mToast.setText(string);
		} else {
			mToast = Toast.makeText(getApplicationContext(), string, Toast.LENGTH_LONG);
		}
		mToast.show();
	}

	protected void hideKeyBoard() {
		// 隐藏键盘
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		if (imm != null) {
			imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
		}
	}
	
	
	/**
	 *Description:显示格式化数值，保留对应分辨率的小数个数，比如传入参数（20.3656，0.01），将返回20.37
	 *@param date 传入的数值
	 *@param radio 保留多少位小数
	 *@return
	 */
	protected String formatValue(double date, Object scale) {
		if (scale instanceof Double) {
			DecimalFormat df = new DecimalFormat(scale.toString());
			return df.format(date);
		}
		return Math.round(date) + "";
	}

}