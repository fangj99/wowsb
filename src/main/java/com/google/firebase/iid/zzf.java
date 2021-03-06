package com.google.firebase.iid;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.ConditionVariable;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.Parcelable;
import android.os.Process;
import android.os.RemoteException;
import android.os.SystemClock;
import android.support.v4.util.SimpleArrayMap;
import android.text.TextUtils;
import android.util.Log;
import com.appsflyer.AppsFlyerProperties;
import com.google.android.gms.common.util.zzt;
import com.google.android.gms.iid.InstanceID;
import com.google.android.gms.iid.MessengerCompat;
import com.yaya.sdk.async.http.AsyncHttpResponseHandler;
import java.io.IOException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.util.Random;

public class zzf {
    static String zzbhQ = null;
    static boolean zzbhR = false;
    static int zzbhS = 0;
    static int zzbhT = 0;
    static int zzbhU = 0;
    static BroadcastReceiver zzbhV = null;
    PendingIntent zzbgG;
    Messenger zzbgK;
    Messenger zzbhX;
    MessengerCompat zzbhY;
    long zzbhZ;
    long zzbia;
    int zzbib;
    int zzbic;
    long zzbid;
    private final SimpleArrayMap<String, zzb> zzclw = new SimpleArrayMap();
    Context zzqn;

    private interface zzb {
        void onError(String str);

        void zzH(Intent intent);
    }

    private static class zza implements zzb {
        private Intent intent;
        private final ConditionVariable zzcly;
        private String zzclz;

        private zza() {
            this.zzcly = new ConditionVariable();
        }

        public void onError(String str) {
            this.zzclz = str;
            this.zzcly.open();
        }

        public void zzH(Intent intent) {
            this.intent = intent;
            this.zzcly.open();
        }

        public Intent zzabT() throws IOException {
            if (!this.zzcly.block(30000)) {
                Log.w("InstanceID/Rpc", "No response");
                throw new IOException(InstanceID.ERROR_TIMEOUT);
            } else if (this.zzclz == null) {
                return this.intent;
            } else {
                throw new IOException(this.zzclz);
            }
        }
    }

    public zzf(Context context) {
        this.zzqn = context;
    }

    public static synchronized String zzHn() {
        String num;
        synchronized (zzf.class) {
            int i = zzbhU;
            zzbhU = i + 1;
            num = Integer.toString(i);
        }
        return num;
    }

    static String zza(KeyPair keyPair, String... strArr) {
        String str = null;
        try {
            byte[] bytes = TextUtils.join("\n", strArr).getBytes(AsyncHttpResponseHandler.DEFAULT_CHARSET);
            try {
                PrivateKey privateKey = keyPair.getPrivate();
                Signature instance = Signature.getInstance(privateKey instanceof RSAPrivateKey ? "SHA256withRSA" : "SHA256withECDSA");
                instance.initSign(privateKey);
                instance.update(bytes);
                str = FirebaseInstanceId.zzv(instance.sign());
            } catch (Throwable e) {
                Log.e("InstanceID/Rpc", "Unable to sign registration request", e);
            }
        } catch (Throwable e2) {
            Log.e("InstanceID/Rpc", "Unable to encode string", e2);
        }
        return str;
    }

    private static boolean zza(PackageManager packageManager) {
        for (ResolveInfo resolveInfo : packageManager.queryIntentServices(new Intent("com.google.android.c2dm.intent.REGISTER"), 0)) {
            if (zza(packageManager, resolveInfo.serviceInfo.packageName, "com.google.android.c2dm.intent.REGISTER")) {
                zzbhR = false;
                return true;
            }
        }
        return false;
    }

    private static boolean zza(PackageManager packageManager, String str, String str2) {
        if (packageManager.checkPermission("com.google.android.c2dm.permission.SEND", str) == 0) {
            return zzb(packageManager, str);
        }
        Log.w("InstanceID/Rpc", new StringBuilder((String.valueOf(str).length() + 56) + String.valueOf(str2).length()).append("Possible malicious package ").append(str).append(" declares ").append(str2).append(" without permission").toString());
        return false;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void zzay(java.lang.String r6, java.lang.String r7) {
        /*
        r5 = this;
        r2 = r5.zzclw;
        monitor-enter(r2);
        if (r6 != 0) goto L_0x0025;
    L_0x0005:
        r0 = 0;
        r1 = r0;
    L_0x0007:
        r0 = r5.zzclw;	 Catch:{ all -> 0x0046 }
        r0 = r0.size();	 Catch:{ all -> 0x0046 }
        if (r1 >= r0) goto L_0x001e;
    L_0x000f:
        r0 = r5.zzclw;	 Catch:{ all -> 0x0046 }
        r0 = r0.valueAt(r1);	 Catch:{ all -> 0x0046 }
        r0 = (com.google.firebase.iid.zzf.zzb) r0;	 Catch:{ all -> 0x0046 }
        r0.onError(r7);	 Catch:{ all -> 0x0046 }
        r0 = r1 + 1;
        r1 = r0;
        goto L_0x0007;
    L_0x001e:
        r0 = r5.zzclw;	 Catch:{ all -> 0x0046 }
        r0.clear();	 Catch:{ all -> 0x0046 }
    L_0x0023:
        monitor-exit(r2);	 Catch:{ all -> 0x0046 }
    L_0x0024:
        return;
    L_0x0025:
        r0 = r5.zzclw;	 Catch:{ all -> 0x0046 }
        r0 = r0.remove(r6);	 Catch:{ all -> 0x0046 }
        r0 = (com.google.firebase.iid.zzf.zzb) r0;	 Catch:{ all -> 0x0046 }
        if (r0 != 0) goto L_0x004f;
    L_0x002f:
        r1 = "InstanceID/Rpc";
        r3 = "Missing callback for ";
        r0 = java.lang.String.valueOf(r6);	 Catch:{ all -> 0x0046 }
        r4 = r0.length();	 Catch:{ all -> 0x0046 }
        if (r4 == 0) goto L_0x0049;
    L_0x003d:
        r0 = r3.concat(r0);	 Catch:{ all -> 0x0046 }
    L_0x0041:
        android.util.Log.w(r1, r0);	 Catch:{ all -> 0x0046 }
        monitor-exit(r2);	 Catch:{ all -> 0x0046 }
        goto L_0x0024;
    L_0x0046:
        r0 = move-exception;
        monitor-exit(r2);	 Catch:{ all -> 0x0046 }
        throw r0;
    L_0x0049:
        r0 = new java.lang.String;	 Catch:{ all -> 0x0046 }
        r0.<init>(r3);	 Catch:{ all -> 0x0046 }
        goto L_0x0041;
    L_0x004f:
        r0.onError(r7);	 Catch:{ all -> 0x0046 }
        goto L_0x0023;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.firebase.iid.zzf.zzay(java.lang.String, java.lang.String):void");
    }

    private Intent zzb(Bundle bundle, KeyPair keyPair) throws IOException {
        String zzHn = zzHn();
        zza com_google_firebase_iid_zzf_zza = new zza();
        synchronized (this.zzclw) {
            this.zzclw.put(zzHn, com_google_firebase_iid_zzf_zza);
        }
        zza(bundle, keyPair, zzHn);
        try {
            Intent zzabT = com_google_firebase_iid_zzf_zza.zzabT();
            synchronized (this.zzclw) {
                this.zzclw.remove(zzHn);
            }
            return zzabT;
        } catch (Throwable th) {
            synchronized (this.zzclw) {
                this.zzclw.remove(zzHn);
            }
        }
    }

    private void zzb(String str, Intent intent) {
        synchronized (this.zzclw) {
            zzb com_google_firebase_iid_zzf_zzb = (zzb) this.zzclw.remove(str);
            if (com_google_firebase_iid_zzf_zzb == null) {
                String str2 = "InstanceID/Rpc";
                String str3 = "Missing callback for ";
                String valueOf = String.valueOf(str);
                Log.w(str2, valueOf.length() != 0 ? str3.concat(valueOf) : new String(str3));
                return;
            }
            com_google_firebase_iid_zzf_zzb.zzH(intent);
        }
    }

    private static boolean zzb(PackageManager packageManager) {
        for (ResolveInfo resolveInfo : packageManager.queryBroadcastReceivers(new Intent("com.google.iid.TOKEN_REQUEST"), 0)) {
            if (zza(packageManager, resolveInfo.activityInfo.packageName, "com.google.iid.TOKEN_REQUEST")) {
                zzbhR = true;
                return true;
            }
        }
        return false;
    }

    private static boolean zzb(PackageManager packageManager, String str) {
        try {
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(str, 0);
            zzbhQ = applicationInfo.packageName;
            zzbhT = applicationInfo.uid;
            return true;
        } catch (NameNotFoundException e) {
            return false;
        }
    }

    public static String zzbA(Context context) {
        if (zzbhQ != null) {
            return zzbhQ;
        }
        zzbhS = Process.myUid();
        PackageManager packageManager = context.getPackageManager();
        if (zzt.zzzq()) {
            if (zzb(packageManager) || zza(packageManager)) {
                return zzbhQ;
            }
        } else if (zza(packageManager) || zzb(packageManager)) {
            return zzbhQ;
        }
        Log.w("InstanceID/Rpc", "Failed to resolve IID implementation package, falling back");
        if (zzb(packageManager, "com.google.android.gms")) {
            zzbhR = zzt.zzzq();
            return zzbhQ;
        } else if (VERSION.SDK_INT >= 21 || !zzb(packageManager, "com.google.android.gsf")) {
            Log.w("InstanceID/Rpc", "Google Play services is missing, unable to get tokens");
            return null;
        } else {
            zzbhR = false;
            return zzbhQ;
        }
    }

    private void zzeF(String str) {
        if ("com.google.android.gsf".equals(zzbhQ)) {
            this.zzbib++;
            if (this.zzbib >= 3) {
                if (this.zzbib == 3) {
                    this.zzbic = new Random().nextInt(1000) + 1000;
                }
                this.zzbic *= 2;
                this.zzbid = SystemClock.elapsedRealtime() + ((long) this.zzbic);
                Log.w("InstanceID/Rpc", new StringBuilder(String.valueOf(str).length() + 31).append("Backoff due to ").append(str).append(" for ").append(this.zzbic).toString());
            }
        }
    }

    void zzHl() {
        if (this.zzbgK == null) {
            zzbA(this.zzqn);
            this.zzbgK = new Messenger(new Handler(this, Looper.getMainLooper()) {
                final /* synthetic */ zzf zzclx;

                public void handleMessage(Message message) {
                    this.zzclx.zze(message);
                }
            });
        }
    }

    void zzHm() {
        synchronized (this) {
            if (zzbhV == null) {
                zzbhV = new BroadcastReceiver(this) {
                    final /* synthetic */ zzf zzclx;

                    {
                        this.zzclx = r1;
                    }

                    public void onReceive(Context context, Intent intent) {
                        if (Log.isLoggable("InstanceID/Rpc", 3)) {
                            String valueOf = String.valueOf(intent.getExtras());
                            Log.d("InstanceID/Rpc", new StringBuilder(String.valueOf(valueOf).length() + 44).append("Received GSF callback via dynamic receiver: ").append(valueOf).toString());
                        }
                        this.zzclx.zzs(intent);
                    }
                };
                if (Log.isLoggable("InstanceID/Rpc", 3)) {
                    Log.d("InstanceID/Rpc", "Registered GSF callback receiver");
                }
                IntentFilter intentFilter = new IntentFilter("com.google.android.c2dm.intent.REGISTRATION");
                intentFilter.addCategory(this.zzqn.getPackageName());
                this.zzqn.registerReceiver(zzbhV, intentFilter, "com.google.android.c2dm.permission.SEND", null);
            }
        }
    }

    Intent zza(Bundle bundle, KeyPair keyPair) throws IOException {
        Intent zzb = zzb(bundle, keyPair);
        if (zzb == null || !zzb.hasExtra("google.messenger")) {
            return zzb;
        }
        zzb = zzb(bundle, keyPair);
        return (zzb == null || !zzb.hasExtra("google.messenger")) ? zzb : null;
    }

    public void zza(Bundle bundle, KeyPair keyPair, String str) throws IOException {
        long elapsedRealtime = SystemClock.elapsedRealtime();
        if (this.zzbid == 0 || elapsedRealtime > this.zzbid) {
            zzHl();
            if (zzbhQ == null) {
                throw new IOException(InstanceID.ERROR_MISSING_INSTANCEID_SERVICE);
            }
            this.zzbhZ = SystemClock.elapsedRealtime();
            Intent intent = new Intent(zzbhR ? "com.google.iid.TOKEN_REQUEST" : "com.google.android.c2dm.intent.REGISTER");
            intent.setPackage(zzbhQ);
            bundle.putString("gmsv", Integer.toString(FirebaseInstanceId.zzR(this.zzqn, zzbA(this.zzqn))));
            bundle.putString("osv", Integer.toString(VERSION.SDK_INT));
            bundle.putString("app_ver", Integer.toString(FirebaseInstanceId.zzcr(this.zzqn)));
            bundle.putString("app_ver_name", FirebaseInstanceId.zzbx(this.zzqn));
            bundle.putString("cliv", "fiid-10298000");
            bundle.putString(AppsFlyerProperties.APP_ID, FirebaseInstanceId.zza(keyPair));
            bundle.putString("pub2", FirebaseInstanceId.zzv(keyPair.getPublic().getEncoded()));
            bundle.putString("sig", zza(keyPair, this.zzqn.getPackageName(), r0));
            intent.putExtras(bundle);
            zzp(intent);
            zzb(intent, str);
            return;
        }
        Log.w("InstanceID/Rpc", "Backoff mode, next request attempt: " + (this.zzbid - elapsedRealtime) + " interval: " + this.zzbic);
        throw new IOException(InstanceID.ERROR_BACKOFF);
    }

    protected void zzb(Intent intent, String str) {
        this.zzbhZ = SystemClock.elapsedRealtime();
        intent.putExtra("kid", new StringBuilder(String.valueOf(str).length() + 5).append("|ID|").append(str).append("|").toString());
        intent.putExtra("X-kid", new StringBuilder(String.valueOf(str).length() + 5).append("|ID|").append(str).append("|").toString());
        boolean equals = "com.google.android.gsf".equals(zzbhQ);
        if (Log.isLoggable("InstanceID/Rpc", 3)) {
            String valueOf = String.valueOf(intent.getExtras());
            Log.d("InstanceID/Rpc", new StringBuilder(String.valueOf(valueOf).length() + 8).append("Sending ").append(valueOf).toString());
        }
        if (equals) {
            zzHm();
            this.zzqn.startService(intent);
            return;
        }
        intent.putExtra("google.messenger", this.zzbgK);
        if (!(this.zzbhX == null && this.zzbhY == null)) {
            Message obtain = Message.obtain();
            obtain.obj = intent;
            try {
                if (this.zzbhX != null) {
                    this.zzbhX.send(obtain);
                    return;
                } else {
                    this.zzbhY.send(obtain);
                    return;
                }
            } catch (RemoteException e) {
                if (Log.isLoggable("InstanceID/Rpc", 3)) {
                    Log.d("InstanceID/Rpc", "Messenger failed, fallback to startService");
                }
            }
        }
        if (zzbhR) {
            this.zzqn.sendBroadcast(intent);
        } else {
            this.zzqn.startService(intent);
        }
    }

    void zze(Message message) {
        if (message != null) {
            if (message.obj instanceof Intent) {
                Intent intent = (Intent) message.obj;
                intent.setExtrasClassLoader(MessengerCompat.class.getClassLoader());
                if (intent.hasExtra("google.messenger")) {
                    Parcelable parcelableExtra = intent.getParcelableExtra("google.messenger");
                    if (parcelableExtra instanceof MessengerCompat) {
                        this.zzbhY = (MessengerCompat) parcelableExtra;
                    }
                    if (parcelableExtra instanceof Messenger) {
                        this.zzbhX = (Messenger) parcelableExtra;
                    }
                }
                zzs((Intent) message.obj);
                return;
            }
            Log.w("InstanceID/Rpc", "Dropping invalid message");
        }
    }

    synchronized void zzp(Intent intent) {
        if (this.zzbgG == null) {
            Intent intent2 = new Intent();
            intent2.setPackage("com.google.example.invalidpackage");
            this.zzbgG = PendingIntent.getBroadcast(this.zzqn, 0, intent2, 0);
        }
        intent.putExtra("app", this.zzbgG);
    }

    String zzq(Intent intent) throws IOException {
        if (intent == null) {
            throw new IOException("SERVICE_NOT_AVAILABLE");
        }
        String stringExtra = intent.getStringExtra("registration_id");
        if (stringExtra == null) {
            stringExtra = intent.getStringExtra("unregistered");
        }
        if (stringExtra != null) {
            return stringExtra;
        }
        stringExtra = intent.getStringExtra("error");
        if (stringExtra != null) {
            throw new IOException(stringExtra);
        }
        String valueOf = String.valueOf(intent.getExtras());
        Log.w("InstanceID/Rpc", new StringBuilder(String.valueOf(valueOf).length() + 29).append("Unexpected response from GCM ").append(valueOf).toString(), new Throwable());
        throw new IOException("SERVICE_NOT_AVAILABLE");
    }

    void zzr(Intent intent) {
        String stringExtra = intent.getStringExtra("error");
        if (stringExtra == null) {
            String valueOf = String.valueOf(intent.getExtras());
            Log.w("InstanceID/Rpc", new StringBuilder(String.valueOf(valueOf).length() + 49).append("Unexpected response, no error or registration id ").append(valueOf).toString());
            return;
        }
        String valueOf2;
        if (Log.isLoggable("InstanceID/Rpc", 3)) {
            valueOf = "InstanceID/Rpc";
            String str = "Received InstanceID error ";
            valueOf2 = String.valueOf(stringExtra);
            Log.d(valueOf, valueOf2.length() != 0 ? str.concat(valueOf2) : new String(str));
        }
        if (stringExtra.startsWith("|")) {
            String[] split = stringExtra.split("\\|");
            if (!"ID".equals(split[1])) {
                String str2 = "InstanceID/Rpc";
                String str3 = "Unexpected structured response ";
                valueOf2 = String.valueOf(stringExtra);
                Log.w(str2, valueOf2.length() != 0 ? str3.concat(valueOf2) : new String(str3));
            }
            if (split.length > 2) {
                valueOf2 = split[2];
                valueOf = split[3];
                if (valueOf.startsWith(":")) {
                    valueOf = valueOf.substring(1);
                }
            } else {
                valueOf = "UNKNOWN";
                valueOf2 = null;
            }
            intent.putExtra("error", valueOf);
        } else {
            valueOf2 = null;
            valueOf = stringExtra;
        }
        zzay(valueOf2, valueOf);
        long longExtra = intent.getLongExtra("Retry-After", 0);
        if (longExtra > 0) {
            this.zzbia = SystemClock.elapsedRealtime();
            this.zzbic = ((int) longExtra) * 1000;
            this.zzbid = SystemClock.elapsedRealtime() + ((long) this.zzbic);
            Log.w("InstanceID/Rpc", "Explicit request from server to backoff: " + this.zzbic);
        } else if ("SERVICE_NOT_AVAILABLE".equals(valueOf) || "AUTHENTICATION_FAILED".equals(valueOf)) {
            zzeF(valueOf);
        }
    }

    void zzs(Intent intent) {
        if (intent != null) {
            String stringExtra;
            String str;
            if ("com.google.android.c2dm.intent.REGISTRATION".equals(intent.getAction())) {
                stringExtra = intent.getStringExtra("registration_id");
                if (stringExtra == null) {
                    stringExtra = intent.getStringExtra("unregistered");
                }
                if (stringExtra == null) {
                    zzr(intent);
                    return;
                }
                this.zzbhZ = SystemClock.elapsedRealtime();
                this.zzbid = 0;
                this.zzbib = 0;
                this.zzbic = 0;
                if (stringExtra.startsWith("|")) {
                    String[] split = stringExtra.split("\\|");
                    if (!"ID".equals(split[1])) {
                        str = "InstanceID/Rpc";
                        String str2 = "Unexpected structured response ";
                        stringExtra = String.valueOf(stringExtra);
                        Log.w(str, stringExtra.length() != 0 ? str2.concat(stringExtra) : new String(str2));
                    }
                    str = split[2];
                    if (split.length > 4) {
                        if ("SYNC".equals(split[3])) {
                            FirebaseInstanceId.zzby(this.zzqn);
                        } else if ("RST".equals(split[3])) {
                            FirebaseInstanceId.zza(this.zzqn, zzd.zzb(this.zzqn, null).zzabQ());
                            intent.removeExtra("registration_id");
                            zzb(str, intent);
                            return;
                        }
                    }
                    stringExtra = split[split.length - 1];
                    if (stringExtra.startsWith(":")) {
                        stringExtra = stringExtra.substring(1);
                    }
                    intent.putExtra("registration_id", stringExtra);
                    stringExtra = str;
                } else {
                    stringExtra = null;
                }
                if (stringExtra != null) {
                    zzb(stringExtra, intent);
                } else if (Log.isLoggable("InstanceID/Rpc", 3)) {
                    Log.d("InstanceID/Rpc", "Ignoring response without a request ID");
                }
            } else if (Log.isLoggable("InstanceID/Rpc", 3)) {
                str = "InstanceID/Rpc";
                String str3 = "Unexpected response ";
                stringExtra = String.valueOf(intent.getAction());
                Log.d(str, stringExtra.length() != 0 ? str3.concat(stringExtra) : new String(str3));
            }
        } else if (Log.isLoggable("InstanceID/Rpc", 3)) {
            Log.d("InstanceID/Rpc", "Unexpected response: null");
        }
    }
}
