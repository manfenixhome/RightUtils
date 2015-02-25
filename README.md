RightUtils
==========
Android library for quick development android application.


## Quick Setup
**Gradle dependency RELEASE:**
``` groovy
compile 'com.rightutils:app:1.2.1@aar'
```
**Gradle dependency SNAPSHOT:**
``` groovy
compile 'com.rightutils:app:1.2.9-SNAPSHOT@aar'

repositories {
	maven { url 'https://oss.sonatype.org/content/repositories/snapshots/' }
}
```
**Maven dependency:**
``` xml
<dependency>
    <groupId>com.rightutils</groupId>
    <artifactId>app</artifactId>
    <version>1.2.1</version>
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

	public Company() {
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
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

####TypedFaceWidgets (released version)
<b>Usage examples</b><br>
Declare font attribute in root layout:
``` xml
xmlns:font="http://schemas.android.com/apk/res-auto"
```
After that, you can use:<br>
<i>TextView with custom fonts</i>
``` xml
<com.rightutils.rightutils.widgets.TypefacedTextView
	android:layout_width="match_parent"
	android:layout_height="wrap_content"
	font:typeface="fonts/AFBattersea.ttf"/>
```
<i>EditText with custom fonts</i>
``` xml
<com.rightutils.rightutils.widgets.TypefacedEditText
	android:layout_width="match_parent"
	android:layout_height="wrap_content"
	font:typeface="fonts/AFBattersea.ttf"/>
```
<i>Button with custom fonts</i>
``` xml
<com.rightutils.rightutils.widgets.TypefacedButton
	android:layout_width="match_parent"
	android:layout_height="wrap_content"
	font:typeface="fonts/AFBattersea.ttf"/>
```
<i>RadioButton with custom fonts</i>
``` xml
<com.rightutils.rightutils.widgets.TypefacedRadioButton
	android:layout_width="match_parent"
	android:layout_height="wrap_content"
	font:typeface="fonts/AFBattersea.ttf"/>
```
<i>CheckBox with custom fonts</i>
``` xml
<com.rightutils.rightutils.widgets.TypefacedCheckbox
	android:layout_width="match_parent"
	android:layout_height="wrap_content"
	font:typeface="fonts/AFBattersea.ttf"/>
```
<i>NB - "fonts/Roboto-Light.ttf" - path and name of font in assets folder. In this case it's mean (assets/fonts/AFBattersea.ttf)</i>

####TypeFaceWidgets (snapshot)

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

