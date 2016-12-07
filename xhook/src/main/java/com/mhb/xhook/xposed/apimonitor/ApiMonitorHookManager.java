package com.mhb.xhook.xposed.apimonitor;


import com.mhb.xhook.logging.BasicLog;
import com.mhb.xhook.logging.XhookLogManager;
import com.mhb.xhook.xposed.apimonitor.ref.AccountManagerHook;
import com.mhb.xhook.xposed.apimonitor.ref.ActivityManagerHook;
import com.mhb.xhook.xposed.apimonitor.ref.ActivityThreadHook;
import com.mhb.xhook.xposed.apimonitor.ref.AlarmManagerHook;
import com.mhb.xhook.xposed.apimonitor.ref.AudioRecordHook;
import com.mhb.xhook.xposed.apimonitor.ref.CameraHook;
import com.mhb.xhook.xposed.apimonitor.ref.ConnectivityManagerHook;
import com.mhb.xhook.xposed.apimonitor.ref.ContentResolverHook;
import com.mhb.xhook.xposed.apimonitor.ref.ContextImplHook;
import com.mhb.xhook.xposed.apimonitor.ref.MediaRecorderHook;
import com.mhb.xhook.xposed.apimonitor.ref.NetWorkHook;
import com.mhb.xhook.xposed.apimonitor.ref.NotificationManagerHook;
import com.mhb.xhook.xposed.apimonitor.ref.PackageManagerHook;
import com.mhb.xhook.xposed.apimonitor.ref.ProcessBuilderHook;
import com.mhb.xhook.xposed.apimonitor.ref.RuntimeHook;
import com.mhb.xhook.xposed.apimonitor.ref.SmsManagerHook;
import com.mhb.xhook.xposed.apimonitor.ref.TelephonyManagerHook;

public class ApiMonitorHookManager {

    protected static final BasicLog LOG = XhookLogManager.getInstance();

    private static ApiMonitorHookManager hookmger;
    private SmsManagerHook smsManagerHook;
    private TelephonyManagerHook telephonyManagerHook;
    private MediaRecorderHook mediaRecorderHook;
    private AccountManagerHook accountManagerHook;
    private ActivityManagerHook activityManagerHook;
    private AlarmManagerHook alarmManagerHook;
    private ConnectivityManagerHook connectivityManagerHook;
    private ContentResolverHook contentResolverHook;
    private ContextImplHook contextImplHook;
    private PackageManagerHook packageManagerHook;
    private RuntimeHook runtimeHook;
    private ActivityThreadHook activityThreadHook;
    private AudioRecordHook audioRecordHook;
    private CameraHook cameraHook;
    private NetWorkHook networkHook;
    private NotificationManagerHook notificationManagerHook;
    private ProcessBuilderHook processBuilderHook;


    private ApiMonitorHookManager(){
        this.smsManagerHook = new SmsManagerHook();
        this.telephonyManagerHook = new TelephonyManagerHook();
        this.mediaRecorderHook = new MediaRecorderHook();
        this.accountManagerHook = new AccountManagerHook();
        this.activityManagerHook = new ActivityManagerHook();
        this.alarmManagerHook= new AlarmManagerHook();
        this.connectivityManagerHook = new ConnectivityManagerHook();
        this.contentResolverHook = new ContentResolverHook();
        this.contextImplHook = new ContextImplHook();
        this.packageManagerHook = new PackageManagerHook();
        this.runtimeHook = new RuntimeHook();
        this.activityThreadHook = new ActivityThreadHook();
        this.audioRecordHook = new AudioRecordHook();
        this.cameraHook = new CameraHook();
        this.networkHook = new NetWorkHook();
        this.notificationManagerHook = new NotificationManagerHook();
        this.processBuilderHook = new ProcessBuilderHook();
    }

    public static ApiMonitorHookManager getInstance(){
        if(hookmger == null)
            hookmger = new ApiMonitorHookManager();
        return hookmger;
    }

    public void startMonitor() {
//        new CoreaaHook().startHook();
//        new NetworkUtilHook().startHook();
//        new ResponseHook().startHook();
//        (new NetworkUtilHook()).startHook();
//        (new ViewHook()).startHook();
//        CommandExecution.CommandResult res1 = CommandExecution.execCommand("ls", false);
//        String[] cmd = {"cd /sdcard", "ls"};
//        CommandExecution.CommandResult res2 = CommandExecution.execCommand(cmd, true);

//        this.smsManagerHook.startHook();
//        this.telephonyManagerHook.startHook();
//        this.mediaRecorderHook.startHook();
//        this.accountManagerHook.startHook();
//        this.activityManagerHook.startHook();
//        this.alarmManagerHook.startHook();
//        this.connectivityManagerHook.startHook();
//        this.contentResolverHook.startHook();
//        this.contextImplHook.startHook();
//        this.packageManagerHook.startHook();
//        this.runtimeHook.startHook();
//        this.activityThreadHook.startHook();
//        this.audioRecordHook.startHook();
//        this.cameraHook.startHook();
//        this.networkHook.startHook();
//        this.notificationManagerHook.startHook();
//        this.processBuilderHook.startHook();
    }

}
