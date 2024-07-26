package com.example.smartsalestracker;

public class Constants {

    public static final int CONNECT_TIMEOUT = 60 * 1000;

    public static final int READ_TIMEOUT = 60 * 1000;

    public static final int WRITE_TIMEOUT = 60 * 1000;

    public static final String BASE_URL = "https://sandbox.safaricom.co.ke/";

    public static final String BUSINESS_SHORT_CODE = "4452386";

    public static final String PASSKEY = "MTc0Mzc5YmZiMjc5ZjlhYTliZGJjZjE1OGU5N2RkNzFhNDY3Y2QyZTBjODkzMDU5YjEwZjc4ZTZiNzJhZGExZWQyYzkxOTIwMjQwNzIyMTIwMzUz";

    public static final String TRANSACTION_TYPE = "CustomerPayBillOnline";

    public static final String PARTYB = "4452386"; //same as business shortcode above

    public static final String CALLBACKURL = "https://mydomain.com/path";
}
