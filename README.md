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
<code>
public class DBUtils extends RightDBUtils {

	private static final String TAG = DBUtils.class.getSimpleName();

	public static DBUtils newInstance(Context context, String name, int version) {
		DBUtils dbUtils = new DBUtils();
		dbUtils.setDBContext(context, name, version);
		return dbUtils;
	}
}
</code>

2) create and init static variable to our Application class:<br>

<code>
public class ExampleApplication extends Application {

	public static DBUtils dbUtils;

	@Override
	public void onCreate() {
		super.onCreate();
		dbUtils = DBUtils.newInstance(this, "example_db.sqlite", 1);
	}
}</code><br>
<b><u>NB "example_db.sqlite" - name of existing database file in assets folder</u></b>

3) finally we can use db:<br>
<b><u>NB Supports fields type
<ul>
<li>all primitive types</li>
<li>String</li>
<li>Long</li>
<li>Integer</li>
<li>Boolean</li>
<li>Float</li>
<li>Double</li>
<li>Date</li>
</ul>
</u></b>

<b>Example usage</b><br>
<code>
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
}</code>

<b><i>Add company:<i></b><br>
add(company);

<b><i>Add list of company:<i></b><br>
addAll(companies);

<b><i>Retrieve all companies from db:<i></b><br>
RightList<Company> companies = getAll(Company.class);

<b><i>Retrieve companies by ids:<i></b><br>
RightList<Company> companies = getAllWhere(String.format("id IN (%s)", TextUtils.join(",", ids)), Company.class);

<b><i>Delete companies by ids:<i></b><br>
deleteWhere(Company.class, String.format("id IN (%s)", TextUtils.join(",", ids)));
	

