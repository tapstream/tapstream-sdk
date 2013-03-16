package com.tapstream.sdk;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import junit.framework.TestCase;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

public class TestRunner {
	public class Util {
		private Context context;
		private ScriptableObject scope;

		public Util(Context context, ScriptableObject scope) {
			this.context = context;
			this.scope = scope;
		}

		public void fail(String message) {
			TestCase.fail(message);
		}

		public void assertEqual(Object a, Object b) {
			TestCase.assertEquals(a, b);
		}

		public void assertTrue(boolean b) {
			TestCase.assertTrue(b);
		}

		public void log(String message) {
			System.out.println(message);
		}

		public String getPostData(Tapstream ts) {
			return ts.core.getPostData();
		}

		public double getDelay(Tapstream ts) {
			return ts.core.getDelay();
		}

		public Scriptable getSavedFiredList(Tapstream ts) {
			Set<String> set = ((PlatformImpl) ts.platform).savedFiredList;
			if (set == null) {
				set = new HashSet<String>();
			}
			return context.newArray(scope, set.toArray());
		}

		public void setResponseStatus(Tapstream ts, int status) {
			((PlatformImpl) ts.platform).response = new Response(status, String.format(Locale.US, "Http %d", status));
		}

		public OperationQueue newOperationQueue() {
			return new OperationQueue();
		}

		public Config newConfig() {
			return new Config();
		}

		public Tapstream newTapstream(OperationQueue queue, String accountName, String secret, Config config) {
			return new Tapstream(queue, accountName, secret, config);
		}

		public Event newEvent(String name, boolean oneTimeOnly) {
			return new Event(name, oneTimeOnly);
		}

		public Hit newHit(String trackerName) {
			return new Hit(trackerName);
		}
	};

	public void run(String[] args) {
		String script = null;
		try {
			FileInputStream stream = new FileInputStream(new File(args[0]));
			try {
				FileChannel fc = stream.getChannel();
				MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
				script = Charset.availableCharsets().get("UTF-8").decode(bb).toString();
			} finally {
				stream.close();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			System.exit(1);
		}
		Context cx = Context.enter();
		try {
			ScriptableObject scope = cx.initStandardObjects();
			scope.putConst("language", scope, "java");
			scope.put("util", scope, new Util(cx, scope));
			cx.evaluateString(scope, script, args[0], 1, null);
		} catch (Error ex) {
			ex.printStackTrace();
			System.exit(1);
		} catch (Exception ex) {
			ex.printStackTrace();
			System.exit(1);
		} finally {
			Context.exit();
		}
		System.exit(0);
	}

	public static void main(String[] args) throws IOException {
		TestRunner tr = new TestRunner();
		tr.run(args);
	}
};
