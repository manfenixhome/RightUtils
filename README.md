RightUtils
==========
It's a light android library for quick development android application.

<h2>For gradle</h2>
Last RELEASE VERSION:


dependencies {
	compile 'com.rightutils:app:1.2.1@aar'
}

Last SNAPSHOT VERSION:

repositories {
	maven { url 'https://oss.sonatype.org/content/repositories/snapshots/' }
}

dependencies {
	compile 'com.rightutils:app:1.2.1-SNAPSHOT@aar'
}

<h2>RightUtils ORM</h2>
<b>SetUp database:</b>

1) create class:<br>
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

2) create and init static variable to our Application class:<br>

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

3) finally we can use db:<br>
<b>Example usage</b><br>
``` java
//Supports fields type: all primitive types, String, Long, Integer, Boolean, Float, Double, Date
public class Company implements Serializable {
	
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

<b><i>Add company:<i></b>
``` java
add(company);
```
<b><i>Add list of company:<i></b>
``` java
addAll(companies);
```
<b><i>Retrieve all companies from db:<i></b>
``` java
RightList<Company> companies = getAll(Company.class);
```
<b><i>Delete all companies from db:<i></b>
``` java
deleteAll(Company.class);
```
<b><i>Retrieve companies by ids:<i></b>
``` java
RightList<Company> companies = getAllWhere(String.format("id IN (%s)", TextUtils.join(",", ids)), Company.class);
```
<b><i>Delete companies by ids:<i></b>
``` java
deleteWhere(Company.class, String.format("id IN (%s)", TextUtils.join(",", ids)));
//or
deleteWhere(Company.class, "id", ids)));
```
	

