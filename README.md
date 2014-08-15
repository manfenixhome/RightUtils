RightUtils
==========
It's a light android library for quick development android application.

For gradle:

repositories {
	maven { url 'https://oss.sonatype.org/content/repositories/snapshots/' }
}
dependencies {
	compile 'com.rightutils:app:1.0.3-SNAPSHOT@aar'
}



<b>SetUp database:</b>

1) create class:<br>

public class DBUtils extends RightDBUtils {

	private static final String TAG = DBUtils.class.getSimpleName();

	public static DBUtils newInstance(Context context, String name, int version) {
		DBUtils dbUtils = new DBUtils();
		dbUtils.setDBContext(context, name, version);
		return dbUtils;
	}
}

2) added and init static variable to our Application class:<br>
public class ExampleApplication extends Application {

	public static DBUtils dbUtils;

	@Override
	public void onCreate() {
		super.onCreate();
		dbUtils = DBUtils.newInstance(this, "example_db.sqlite", 1);
	}
}

3) finally we can use db:<br>
Code examples:<br>

public class Company implements Serializable {<br>
	private long id;<br>
	private String name;<br>

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

Add company:<br>
add(company);

Add list of company:<br>
addAll(companies);

Retrieve all companies from db:<br>
RightList<Company> companies = getAll(Company.class);

Retrieve companies by ids:<br>
RightList<Company> companies = getAllWhere(String.format("id IN (%s)", TextUtils.join(",", ids)), Company.class);

Delete companies by ids:<br>
deleteWhere(Company.class, String.format("id IN (%s)", TextUtils.join(",", ids)));
	

