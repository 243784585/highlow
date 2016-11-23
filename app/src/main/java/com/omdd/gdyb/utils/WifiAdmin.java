package com.omdd.gdyb.utils;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;

import java.util.List;

public class WifiAdmin {
    private WifiManager wifiManager;// 声明管理对象
    private WifiInfo wifiInfo;// Wifi信息
    private List<ScanResult> scanResultList; // 扫描出来的网络连接列表
    private List<WifiConfiguration> wifiConfigList;// 网络配置列表
    private WifiLock wifiLock;// Wifi锁

    public WifiAdmin(Context context) {
        this.wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);// 获取Wifi服务
        // 得到Wifi信息
        this.wifiInfo = wifiManager.getConnectionInfo();// 得到连接信息
    }

    public boolean getWifiStatus() {
        return wifiManager.isWifiEnabled();
    }

    // 打开/关闭 wifi
    public boolean openWifi() {
        if (!wifiManager.isWifiEnabled()) {
            return wifiManager.setWifiEnabled(true);
        } else {
            return false;
        }
    }

    public boolean isWifiEnabled(){
        return wifiManager.isWifiEnabled();
    }

    public boolean closeWifi() {
        if (!wifiManager.isWifiEnabled()) {
            return true;
        } else {
            return wifiManager.setWifiEnabled(false);
        }
    }

    // 锁定/解锁wifi
    // 其实锁定WiFI就是判断wifi是否建立成功，在这里使用的是held，握手的意思acquire 得到！
    public void lockWifi() {
        wifiLock.acquire();
    }

    public void unLockWifi() {
        if (!wifiLock.isHeld()) {
            wifiLock.release(); // 释放资源
        }
    }

    // 我本来是写在构造函数中了，但是考虑到不是每次都会使用Wifi锁，所以干脆自己建立一个方法！需要时调用，建立就OK
    public void createWifiLock() {
        wifiLock = wifiManager.createWifiLock(WifiManager.WIFI_MODE_FULL_HIGH_PERF,"flyfly"); // 创建一个锁的标志
    }

    // 扫描网络
    public void startScan() {
        wifiManager.startScan();
        scanResultList = wifiManager.getScanResults();

        // 扫描返回结果列表
        wifiConfigList = wifiManager.getConfiguredNetworks(); // 扫描配置列表
    }

    public List<ScanResult> getWifiList() {
        return scanResultList;
    }

    public List<WifiConfiguration> getWifiConfigList() {
        return wifiConfigList;
    }

    // 获取扫描列表
    public StringBuilder lookUpscan() {
        StringBuilder scanBuilder = new StringBuilder();
        for (int i = 0; i < scanResultList.size(); i++) {
            scanBuilder.append("编号：" + (i + 1));
            scanBuilder.append(scanResultList.get(i).toString());
            //所有信息
            scanBuilder.append("\n");
        }
        return scanBuilder;
    }

    //获取指定信号的强度
    public int getLevel(int NetId) {
        return scanResultList.get(NetId).level;
    }

    // 获取本机Mac地址
    public String getMac() {
        return (wifiInfo == null) ? "" : wifiInfo.getMacAddress();
    }

    public String getBSSID() {
        return (wifiInfo == null) ? null : wifiInfo.getBSSID();
    }

    public String getSSID() {
        try {
            return wifiManager.getConnectionInfo().getSSID();
        }catch (Exception e){
            return null;
        }
    }

    // 返回当前连接的网络的ID
    public int getCurrentNetId() {
        return (wifiInfo == null) ? null : wifiInfo.getNetworkId();
    }

    // 返回所有信息
    public String getwifiInfo() {
        return (wifiInfo == null) ? null : wifiInfo.toString();
    }

    // 获取IP地址
    public int getIP() {
        return (wifiInfo == null) ? null : wifiInfo.getIpAddress();
    }

    // 添加一个连接
    public boolean addNetWordLink(WifiConfiguration config) {
        return wifiManager.enableNetwork(wifiManager.addNetwork(config), true);
    }

    // 禁用一个链接
    public boolean disableNetWordLick(int NetId) {
        wifiManager.disableNetwork(NetId);
        return wifiManager.disconnect();
    }

    // 移除一个链接
    public boolean removeNetworkLink(int NetId) {
        return wifiManager.removeNetwork(NetId);
    }

    //不显示SSID
    public void hiddenSSID(int NetId) {
        wifiConfigList.get(NetId).hiddenSSID = true;
    }

    //显示SSID
    public void displaySSID(int NetId) {
        wifiConfigList.get(NetId).hiddenSSID = false;
    }

    public WifiConfiguration CreateWifiInfo(String SSID, String Password, int Type) {
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        config.SSID = "\"" + SSID + "\"";

        WifiConfiguration tempConfig = this.IsExsits(SSID);
        if (tempConfig != null) {
            wifiManager.removeNetwork(tempConfig.networkId);
        }

        if (Type == 1) //WIFICIPHER_NOPASS
        {
            config.wepKeys[0] = "";
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        } else if (Type == 2) //WIFICIPHER_WEP
        {
            config.hiddenSSID = true;
            config.wepKeys[0] = "\"" + Password + "\"";
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        } else if (Type == 3) //WIFICIPHER_WPA
        {
            config.preSharedKey = "\"" + Password + "\"";
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            //config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            config.status = WifiConfiguration.Status.ENABLED;
        }
        return config;
    }

    private WifiConfiguration IsExsits(String SSID) {
        List<WifiConfiguration> existingConfigs = wifiManager.getConfiguredNetworks();
        for (WifiConfiguration existingConfig : existingConfigs) {
            if (existingConfig.SSID.equals("\"" + SSID + "\"")) {
                return existingConfig;
            }
        }
        return null;
    }

    // 指定配置好的网络进行连接
    public void connectConfiguration(int index) {
        // 索引大于配置好的网络索引返回
        if (index > wifiConfigList.size()) {
            return;
        }
        // 连接配置好的指定ID的网络
        wifiManager.enableNetwork(wifiConfigList.get(index).networkId,
                true);
    }
}