package com.projects.em;

import android.text.TextUtils;


public class ListItemClass {
	//String Items
	public static final int MAIN_ITEM = 0;//Name(for  GROUP,Trip,Member,Event)
	private static final int SUBITEM_1 = 1;
	public static final int DATE = SUBITEM_1;
	public static final int PHNO = SUBITEM_1;//Date(for Event and TRIP) PhNo(For Member)
	public static final int TRIPNAME = SUBITEM_1;//Tripname for Child Result page
	public static final int str_BILL = SUBITEM_1;//Amount(Event,Share,Result)
	private static final int SUBITEM_2 = 2;
	public static final int EMAIL=SUBITEM_2 ;
	public static final int PLACE = SUBITEM_2;//Place(for Event and TRIP) Email(For Member)
	public static final int PIC_FILE = 3;//PIC File(for  Trip,Member,Event)
	public static final int DESC = 4;//DESC(for  Trip,Member,Event)
	private static final int EXTRASTRING1 = 5;
	public static final int UNAME = EXTRASTRING1;
//Int Items
	public static final int ID = 0;// Base Ids(for  Trip,Member,Event,Share,TripMember)
		public static final int ID_AddNewItem=-100;
	private static final int REF_ID1 = 1;
	public static final int EVENTID_FORSHARES = REF_ID1;
	public static final int GROUPID_FORTRIPS = REF_ID1;
	public static final int TRIPID_FOREVENT = REF_ID1;// TripId(FOR Event/TripMember) or EventId(For Share) or Groupid for trips
	private static final int REF_ID2 = 2;
	public static final int PAIDBY_MEMBERID = REF_ID2;
	public static final int MEMBERID_FORTRIP = REF_ID2;// MemberId(For TripMember) or PaidById(For Share)
	private static final int REF_ID3 = 3;
	public static final int SHAREDBY_MEMBERID = REF_ID3; 
	public static final int BILL = 4;//Amount(Event,Share,Result)
	public static final int THEME = 5;//For Trips or ResultPage
		public static final int PAY_THEME = 100;
		public static final int RECIEVE_THEME = 101;		
	public static final int CLASSFOR=6;//For Member,Trip,Event,
		private static final int CLASSFOR_TRIPS=1;
		public static final int CLASSFOR_EVENTS=2;
		private static final int CLASSFOR_MEMBERS=3;
		private static final int CLASSFOR_SHARES=4;
		private static final int CLASSFOR_RESULTPARENT=5;
		private static final int CLASSFOR_RESULTCHILD=6;
		private static final int CLASSFOR_TRIPMEMBER=7;
		public static final int CLASSFOR_GROUP=8;
	public static final int TYPE=7;//For Member,Trip,Event
	public static final int OLD_ID=8;	
	private String StringItems[] = { "", "", "" ,"","",""};
	private int IntItems[] = { -1, -1, -1 ,0,0,0,0,0,-1};
	private float Amount = 0f;
	private boolean check = false;//Checkbox Status
	private boolean chkEnabled = true;
	

/**
 * @param ClassFor: Type of The Class (Values from CLASSFOR_****)
 * @param id :Base ID
 * @param refid1: Date(for Event and TRIP) PhNo(For Member)
 * @param refid2: Place(for Event and TRIP) Email(For Member)
 * @param mainItem: Name(for  Trip,Member,Event)
 * @param subItem1: Date(for Event and TRIP) PhNo(For Member)
 * @param subItem2: Place(for Event and TRIP) Email(For Member)
 * @param Bill: For Events,Share and Result
 * @param Theme: For Trips or ResultPage
 * @param type: For Type of item as a part of some groupid
 * @param picFile: PIC File(for  Trip,Member,Event)
 * @param desc: Desc
 */

	
	private ListItemClass(int ClassFor,int id, int refid1, int refid2, 
		String mainItem,String subItem1, String subItem2, float Bill,
		int Theme,int type,String picFile,String desc) {
	this.IntItems[ID] = id;
	this.IntItems[REF_ID1] = refid1;
	this.IntItems[REF_ID2] = refid2;
	
	this.IntItems[BILL]=(int)Bill;
	this.IntItems[THEME]=Theme;
	this.IntItems[CLASSFOR]=ClassFor;
	this.IntItems[TYPE]=type;
	

	this.StringItems[MAIN_ITEM] = mainItem;
	this.StringItems[SUBITEM_1] = subItem1;
	this.StringItems[SUBITEM_2] = subItem2;
	if(TextUtils.isEmpty(desc))
	{
		desc="No Desc";
		//Log.w("Desc Updated","No Desc");
	}
	this.StringItems[DESC] = desc;
	
	this.StringItems[PIC_FILE] = picFile;
	this.Amount = Bill;
}


	/**
	 * For Members in Database
	 * @param id
	 * @param name
	 * @param Phno
	 * @param email
	 * @param pic 
	 * @param type
	 * @param Desc
	 */
	public ListItemClass(int id, String name,String Phno,String email,String pic,int type, String Desc,String uname)

	{
		this(CLASSFOR_MEMBERS,id, -1, -1, name, Phno, email, 0, 0,type,pic,Desc);
		this.setItemAsked(UNAME, uname);
	}

	/**
	 * For Events in Database
	 * @param id
	 * @param name
	 * @param bill
	 * @param TripId
	 * @param date
	 * @param Place
	 * @param type
	 * @param desc
	 */
	public ListItemClass(int id, String name, float bill, int TripId,String date,String Place,String picFile,String desc) {
		// TODO Auto-generated constructor stub
		this(CLASSFOR_EVENTS,id, TripId,-1, name, date, Place, bill, 0,0,picFile,desc);
	}
	/**
	 * For GROUPS in database
	 * @param id
	 * @param GroupName
	 * @param Theme
	 * @param type
	 * @param Desc
	 */
		public ListItemClass(int id, String GroupName,  int Theme,int type,String Desc) {
			this(CLASSFOR_GROUP,id, -1, -1, GroupName, "", "", 0, Theme,type,"",Desc);
		}

/**
 * For Trips in database
 * @param id
 * @param TripName
 * @param date
 * @param place
 * @param picFile
 * @param Theme
 * @param type
 * @param Desc
 */
	public ListItemClass(int id, String TripName, String date,String place, String picFile, int Theme,int type,String Desc,int GroupId) {
		this(CLASSFOR_TRIPS,id, GroupId, -1, TripName, date, place, 0, Theme,type,picFile,Desc);
	}


	public ListItemClass() {
	}

	/**
	 * For TripMember database
	 * @param TripMemberId
	 * @param TripId
	 * @param MemberId
	 */
	public ListItemClass(int TripMemberId, int TripId, int MemberId) {
		this(CLASSFOR_TRIPMEMBER,TripMemberId, TripId, MemberId, "", "", "", 0, 0,0,"","");
	};
/**
 * For Share Database
 * @param ShareId
 * @param EventId
 * @param PaidByMemberId
 * @param SharedByMemberId
 * @param amount
 */
	public ListItemClass(int ShareId, int EventId, int PaidByMemberId,
			int SharedByMemberId, float amount) {
		this(CLASSFOR_SHARES,ShareId, EventId, PaidByMemberId, "", "", "", amount, 0,0,"","");
		setItemAsked(SHAREDBY_MEMBERID, SharedByMemberId);
		
	}
	
	 /**
	  *Result Page Parent Format 
	  * @param memberid(its the Parents ID)
	  * @param memberName
	  * @param Amount
	  * @param Theme
	  */
	 public ListItemClass(int memberid, String  memberName, String Amount,int Theme,float amountF)
	{
		this(CLASSFOR_RESULTPARENT,memberid, -1, -1, memberName, "To Pay/Recieve", Amount, amountF, Theme,0,"","");
	}
	
	/**
	 * Result Page Child Format 
	 * @param eventid(its the Parents ID)
	 * @param eventName
	 * @param TripName
	 * @param Amount
	 * @param Format
	 */
	public ListItemClass(int eventId, String  eventName, String TripName,String Amount,int Theme,float amountF)
	{
		this(CLASSFOR_RESULTCHILD,eventId, -1, -1,eventName, TripName, Amount, amountF, Theme,0,"","");
	}
	// Set Values

	public float getAmount() {
		return Amount;
	}

	public boolean getCheck() {
		return check;
	}

	public int getIntItemAsked(int whichOne) {
		switch (whichOne) {
		case ID:
		case REF_ID1:
		case REF_ID2:
		case REF_ID3:
		case OLD_ID:
		case THEME:
		case CLASSFOR:
			return IntItems[whichOne];
		default:
			return -1;
		}
	}

	public int[] getIntValues() {
		return IntItems;
	}

	public String getStringItemAsked(int whichOne) {
		switch (whichOne) {
		case MAIN_ITEM:
		case SUBITEM_1:
		case SUBITEM_2:
		case PIC_FILE:
		case DESC:
		case EXTRASTRING1:
			return StringItems[whichOne];
		default:
			return "";
		}
	}

	// Get Values

	public String[] getStringValues() {
		return StringItems;
	}

	/**
	 * @return the chkEnabled
	 */
	public boolean isChkEnabled() {
		return chkEnabled;
	}

	public void setCheck(boolean flag) {
		check = flag;
		// Log.w("CLass", "It Is set to " + flag);
	}

	/**
	 * @param chkEnabled
	 *            the chkEnabled to set
	 */
	public void setChkEnabled(boolean chkEnabled) {
		this.chkEnabled = chkEnabled;
	}

	public void setItemAsked(int whichOne, float value) {
		switch (whichOne) {
		case BILL:
			Amount = value;
		}
	}
	public void setItemAsked(int whichOne, double value) {
		setItemAsked(whichOne, (float)value);
	}
	public void setItemAsked(int whichOne, int value) {
		switch (whichOne) {
		case ID:
		case REF_ID1:
		case REF_ID2:
		case REF_ID3:
		case THEME:
		case CLASSFOR:
		case OLD_ID:
			IntItems[whichOne] = value;
			break;
		case BILL:
			Amount = value;
			break;
		}
	}

	public void setItemAsked(int whichOne, String value) {

		switch (whichOne) {
		case MAIN_ITEM:
		case SUBITEM_1:
		case SUBITEM_2:
		case PIC_FILE:
		case DESC:
		case EXTRASTRING1:
			StringItems[whichOne] = value;
			break;
		}

	}

	public void setItemAsked(int[] values) {
		IntItems = values;
	}

	public void setItemAsked(String[] values) {
		StringItems = values;
	}

	public int toInt() {
		return IntItems[0];
	}

	@Override
	public String toString() {
		return StringItems[0];
	}

}
