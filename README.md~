RightUtils
==========
Android library which doing more easy development application for android. The library contains part for working with db, some widgets, loaders, requests for REST API and other tasty features.

## Quick Setup
**Gradle dependency RELEASE:**
``` groovy
compile 'com.rightutils:app:1.4.0@aar'
```
**Gradle dependency SNAPSHOT:**
``` groovy
compile 'com.rightutils:app:1.4.0-SNAPSHOT@aar'

repositories {
	maven { url 'https://oss.sonatype.org/content/repositories/snapshots/' }
}
```
**Maven dependency:**
``` xml
<dependency>
    <groupId>com.rightutils</groupId>
    <artifactId>app</artifactId>
    <version>1.4.0</version>
</dependency>
```

<h2>ORM</h2>
####SetUp database:

#####1) create class:<br>
``` java
public class DBUtils extends RightDBUtils {

	private static final String TAG = DBUtils.class.getSimpleName();

	public static DBUtils newInstance(Context context, String name, int version) {
		DBUtils dbUtils = new DBUtils();
		dbUtils.setDBContext(context, name, version);
		return dbUtils;
	}
}
```

#####2) create and init static variable to your Application class:<br>

``` java
public class ExampleApplication extends Application {

	public static DBUtils dbUtils;

	@Override
	public void onCreate() {
		super.onCreate();
		//example_db.sqlite - name of existing database file in assets folder
		dbUtils = DBUtils.newInstance(this, "example_db.sqlite", 1);
	}
}
```

####How to use:<br>
``` java
//Supports fields type: all primitive types, String, Long, Integer, Boolean, Float, Double, Date
//Use @TableName("table_name") annotation if name are different
public class Company {
	//Use @ColumnName("_id") annotation if column name are different.
	//Use @ColumnIgnore annotation if this field not saving in database
	private long id;
	private String name;
}
```

<i>Add company:<i>
``` java
add(company);
```
<i>Add list of company:<i>
``` java
add(companies);
```
<i>Retrieve all companies from db:<i>
``` java
RightList<Company> companies = getAll(Company.class);
```
<i>Delete all companies from db:<i>
``` java
deleteAll(Company.class);
```
<i>Retrieve companies by ids:<i>
``` java
RightList<Company> companies = getAllWhere(String.format("id IN (%s)", TextUtils.join(",", ids)), Company.class);
```
<i>Delete companies by ids:<i>
``` java
deleteWhere(Company.class, String.format("id IN (%s)", TextUtils.join(",", ids)));
//or
deleteWhere(Company.class, "id", ids)));
```

<h2>WIDGETS</h2>
Put custom fonts to assets library.
<i>NB - "fonts/AFBattersea.ttf" - path and name of font in assets folder. In this case it's mean (assets/fonts/AFBattersea.ttf)</i>

####TypeFaceWidgets

#####1) Specify the same typeface for all widgets in application theme.
``` xml
    <!-- Base application theme. -->
    <style name="AppTheme" parent="Theme.AppCompat.Light.DarkActionBar">
		<item name="customTypefaceStyle">@style/TypefaceStyle</item>
	</style>

	<style name="TypefaceStyle">
		<item name="typeface">fonts/Roboto-Light.ttf</item>
	</style>

```
#####2) Specify typefaces for each type of widgets in application theme.

``` xml
    <!-- Base application theme. -->
    <style name="AppTheme" parent="Theme.AppCompat.Light.DarkActionBar">
        <item name="android:textViewStyle">@style/TextViewStyle</item>
        <item name="android:buttonStyle">@style/ButtonStyle</item>
        <item name="android:editTextStyle">@style/EditStyle</item>
        <item name="android:radioButtonStyle">@style/RadioButton</item>
        <item name="android:checkboxStyle">@style/CheckBoxStyle</item>
    </style>
```
Each style has to inherited extend default parent element style to keep all attributes, e.g. 

``` xml
    <style name="TextViewStyle" parent="android:Widget.TextView">
		<item name="typeface">fonts/Roboto-Regular.ttf</item>
	</style>

    <style name="EditTextStyle" parent="android:Widget.EditText">
        <item name="typeface">fonts/Roboto-Regular.ttf</item>
    </style>

    <style name="RadioButtonStyle" parent="android:Widget.CompoundButton.RadioButton">
        <item name="typeface">fonts/Roboto-Regular.ttf</item>
    </style>

    <style name="ButtonStyle" parent="android:Widget.Button">
        <item name="typeface">fonts/Roboto-Regular.ttf</item>
    </style>

    <style name="CheckBoxStyle" parent="android:Widget.CompoundButton.CheckBox">
        <item name="typeface">fonts/Roboto-Regular.ttf</item>
    </style>
```

Then you can add new styles extended already defined.
``` xml
    <style name="EditStyle.Login">
        <item name="android:textColor">@android:color/white</item>
        <item name="android:textColorHint">@android:color/white</item>
        <item name="typeface">fonts/OpenSans-Regular.ttf</item>
    </style>

    <style name="TextViewStyle.Bold">
        <item name="typeface">fonts/OpenSans-Bold.ttf</item>
    </style>
```
 
#####3) Specify font in style.
``` xml
	<style name="TextViewStyle" parent="android:Widget.TextView">
		<item name="typeface">fonts/Roboto-Regular.ttf</item>
	</style>
```
Set style for widget directly in layout xml file.
``` xml
	<com.rightutils.rightutils.widgets.TypefaceTextView
		android:layout_width="match_parent"
		android:text="Lorem Ipsum style"
		android:layout_height="wrap_content"
		style="@style/TypefaceStyle"/>
```
#####4) Specify font directly in widget's element in layout xml file.
 Declare font tag in root element of layout xml file.
``` xml
xmlns:font="http://schemas.android.com/apk/res-auto"
```
``` xml
	<com.rightutils.rightutils.widgets.TypefaceTextView
		android:layout_width="match_parent"
		android:text="Lorem Ipsum style"
		android:layout_height="wrap_content"
		font:typeface="fonts/Roboto-Regular.ttf"/>
```

<h2>LOADERS</h2>
####How to use:<br>
First of all you need create the class which extends from BaseLoader<T> class<br>
``` java
public class CustomLoader extends BaseLoader<SomeType> {

	private static final String TAG = CustomLoader.class.getSimpleName();
	private static final int loaderId = 1; // unique identificator in loader manager

	public CustomLoader(FragmentActivity fragmentActivity) {
		super(fragmentActivity, loaderId);
	}

	@Override
	public SomeType loadInBackground() {
		//TODO something
		return someType;
	}
}
```
After that you can use your loader in activities or fragments<br>
``` java
new CustomLoader(this)
	//optional param, 'false' - loader can't be canceled. 'true' - loader can be canceled; by default 'true'
	.setCancelable(false)
	//optional param, if need access to fragment after execution
	.setContainer(LoaderFragment.class)
	//optional param, you can use standart dialog themes or customs;
	.setTheme(android.R.style.Theme_Holo_Dialog)
	.setLoaderListener(new BaseLoaderListener<SomeType>() {
		@Override
		public void onLoadFinished(FragmentActivity activity, Fragment fragmentContainer, SomeType data, BaseLoader<SomeType> loader) {
			//activity - always valid activity, even after rotate screen
			//fragmentContainer - can be null if setContainer not invoke or fragment was changed
			//do something with result
		}
	})
	.execute();
```
<h2>REQUEST</h2>
Requests for REST API which support SNI
####How to use:<br>
By default you can use basic implementation RightRequest interface - BasicRightRequest which contains several type of requests:
``` java
HttpResponse getHttpResponse(String url) throws Exception;

HttpResponse getHttpResponse(String url, RequestConfig config) throws Exception;

HttpResponse getHttpResponse(String url, Header[] headers) throws Exception;

HttpResponse getHttpResponse(String url, Header header) throws Exception;

HttpResponse postHttpResponse(String url, List<NameValuePair> nameValuePairs) throws Exception;

HttpResponse postHttpResponse(String url, Header header, List<NameValuePair> nameValuePairs) throws Exception;

HttpResponse postHttpResponse(String url, Header[] headers, List<NameValuePair> nameValuePairs) throws Exception;

HttpResponse postHttpResponse(String url, HttpEntity entity) throws Exception;

HttpResponse postHttpResponse(String url, Header header, HttpEntity entity) throws Exception;

HttpResponse postHttpResponse(String url, StringEntity entity) throws Exception;

HttpResponse postHttpResponse(String url, String json) throws Exception;

HttpResponse postHttpResponse(String url, Header header, String json) throws Exception;

HttpResponse deleteHttpResponse(String url) throws Exception;

HttpResponse deleteHttpResponse(String url, Header header) throws Exception;

HttpResponse putHttpResponse(String url, List<NameValuePair> nameValuePairs) throws Exception;

HttpResponse putHttpResponse(String url, String json) throws Exception;

HttpResponse putHttpResponse(String url, HttpEntity entity) throws Exception;

HttpResponse putHttpResponse(String url, StringEntity entity) throws Exception;

HttpResponse putHttpResponse(String url, Header header, String json) throws Exception;

HttpResponse putHttpResponse(String url, Header[] headers, List<NameValuePair> nameValuePairs) throws Exception;

HttpResponse putHttpResponse(String url, Header header, List<NameValuePair> nameValuePairs) throws Exception;

HttpResponse putHttpResponse(String url, Header header) throws Exception;

HttpResponse putHttpResponse(String url, Header header, HttpEntity entity) throws Exception;
```
But if you need some another type of request you can create your class which extends from BasicRightRequest class and add you methods there.

Usage example:
``` java
@Override
protected String doInBackground(Void... params) {
	try {
		HttpResponse response = new BasicRightRequest().getHttpResponse(PROFILE_URL, new BasicHeader("Authorization", cache.getAuthToken()));
		int status = response.getStatusLine().getStatusCode();
		Log.i(TAG, "status code: " + String.valueOf(status));
		if (status == HttpStatus.SC_OK) {
			return EntityUtils.toString(response.getEntity());
		}
	} catch (Exception e) {
		Log.e(TAG, "run", e);
	}
	return null;
}
```

# RightBaseLazyLoader

#### Set Up:

	public class RightLoader extends RightBaseLazyLoader{
		public RightLoader(FragmentActivity fragmentActivity, int loaderId) {
			super(fragmentActivity, loaderId,SystemUtils.MAPPER);

			// default setup
			setResponseListener(HttpStatus.SC_FORBIDDEN, new CallbackResponse<String>() {
				@Override
				public void response(int pageCode, String response,FragmentActivity fragmentActivity, Fragment fragment) throws Exception {

				}
			});
			setResponseListener(HttpStatus.SC_BAD_REQUEST, new CallbackResponse<String>() {
				@Override
				public void response(int pageCode, String response,FragmentActivity fragmentActivity, Fragment fragment) throws Exception {

				}
			});
		}
	}
	
#### Usage:

	new RightLoader(this,SystemUtils.LOADER_FRIENDS).setRequest(new GetFriendsRequest(this))
		.setResponseListener(HttpStatus.SC_OK, new RightBaseLazyLoader.CallbackResponse<String>() {
			@Override
			public void response(int pageCode, String response,FragmentActivity fragmentActivity, Fragment fragment) throws Exception {
				// success
				}
			}).execute();
##### Simple GET Example:

	public class GetFriendsRequest extends LazyRequest {
		private Context context;
		private Cache cache;

		public GetFriendsRequest(Context context) {
			this.context = context;
		}

		@Override
		public Header getHeader() {
			SystemUtils.getCache(context, new CacheUtils.CallBack<Cache>() {
				@Override
				public boolean run(Cache cache) {
					GetFriendsRequest.this.cache = cache;
					return false;
			    	}
			});
			return new BasicHeader(SystemUtils.APIKEY_PARAM, cache.getAuthToken());
		}

		@Override
		public String getUrl() {
			return SystemUtils.API_URL_FRIENDS;
		}

	}
##### Simple POST Example:
	public class CreateReminderRequest extends LazyRequest {
		private Cache cache;
		private ImportantDate importantDate;

		public CreateReminderRequest(Cache cache, ImportantDate importantDate) {
			this.cache = cache;
			this.importantDate = importantDate;
		}

		@Override
		public String getPostJson() throws JSONException {
			JSONObject jsonPOst = new JSONObject();
			jsonPOst.put("reminder",new JSONObject().put("date", importantDate.getStart()));
			return jsonPOst.toString();
		}

		@Override
		public String getUrl() {
			return String.format(SystemUtils.API_URL_POST_REMINDER,importantDate.getId());
		}

		@Override
		public Header getHeader() {
			return new BasicHeader(SystemUtils.APIKEY_PARAM, cache.getAuthToken());
		}
	}

##### Custom Example:
	public class DeleteReminderRrequest extends LazyRequest {
		private Cache cache;
		private ImportantDate importantDate;

		public DeleteReminderRrequest(Cache cache, ImportantDate importantDate) {
			this.cache = cache;
			this.importantDate = importantDate;
		}

		@Override
		public String getUrl() {
			return String.format(SystemUtils.API_URL_DELETE_REMINDER,importantDate.getId());
		}

		@Override
		public HttpResponse getCustomResponse() throws Exception {
			BasicRightRequest brr = new BasicRightRequest();
			return brr.deleteHttpResponse(getUrl(),new BasicHeader(SystemUtils.APIKEY_PARAM, cache.getAuthToken()));
		}
	}
##### Secure GET Example:
	public class SearchUserRequest extends LazyRequest {
		private Context context;
		private Cache cache;
		private String identity;

		public SearchUserRequest(Context context,String identity) {
			this.identity = identity;
			this.context = context;
		}

		@Override
		public String getUrl() {
			return String.format(SystemUtils.API_URL_SEARCH_BYEMAILID,identity);
		}

		@Override
		public Header getHeader() {
			SystemUtils.getCache(context, new CacheUtils.CallBack<Cache>() {
				@Override
				public boolean run(Cache cache) {
					SearchUserRequest.this.cache = cache;
					return false;
				}
			});
			return new BasicHeader(SystemUtils.APIKEY_PARAM, cache.getAuthToken());
		}
	}
	 
##### Complicated Example:
	
	public class UpdateProfileRequest extends AddMultipartEntityBuilderToLazyRequest {
		private Context context;
		private Cache cache;

		public UpdateProfileRequest(Context context,Cache cache) {
			super(context);
			this.context = context;
			this.cache = cache;
		}

		@Override
		public String getUrl() {
			return SystemUtils.API_URL_UPDATE_PROFILE;
		}

		@Override
		public Header getHeader() {
			return new BasicHeader(SystemUtils.APIKEY_PARAM, cache.getAuthToken());
		}
	}

	UpdateProfileRequest request = new UpdateProfileRequest(this);
	request.addPart("user[firstName]",edtFirstName.getText().toString());
	request.addPart("user[lastName]",edtSureName.getText().toString());
	request.addPart("user[gender]",Integer.toString(gender));
	request.addPart("user[birthday]",DateUtils.calendarToJson(birthday));
	if(profiileImageURI != null) {
		request.addPart("picture",profiileImageURI);
	}

	new RightLoader(this,SystemUtils.LOADER_UPDATE_PROFILE)
		.setRequest(request)
		.setResponseListener(HttpStatus.SC_OK, new RightBaseLazyLoader.CallbackResponse<String>() {
			@Override
			public void response(int pageCode, String response,FragmentActivity fragmentActivity, Fragment fragment) throws Exception {
		                // success
			}
		}).execute();

	

##### Friendly response:
	public class SingleUserResponse implements RightResponse {
		public User user;

		public SingleUserResponse() {
		}

		@Override
		public boolean isValid() {
			return user != null;
		}

		@Override
		public String toString() {
			return "SingleUserResponse{" +
			"user=" + user +
			'}';
		}
	}
