package com.ecitta.android.support;

/**
 * Created by Swapnil.Patel on 7/21/2016.
 */
public class AppConstant {

    public static final String base_url = "http://45.63.108.141:8086/";

    public static final String login = base_url + "Token";
    public static final String login_details = base_url + "api/GetUserDetails";
    public static final String residence_details = base_url + "api/CompanyResidence";
    public static final String update_profile = base_url + "api/UpdateProfile";
    public static final String process_details = base_url + "api/GetCompanyProcess";
    public static final String dashboard_details = base_url + "api/GetCompanyDashboard";
    public static final String customer_details = base_url + "api/GetCompanyCustomer";
    public static final String doc_details = base_url + "api/GetCompanyDocumentById";
    public static final String flag_logo = base_url + "api/GetFlagLogo";
    public static final String todolist = base_url + "api/Todo";
    public static final String message_list = base_url + "api/GetMessageByUserId";
    public static final String message_delete = base_url + "api/DeleteMessageByCompany";
    public static final String message_delete_All = base_url + "api/DeleteAllMessageByCompany";
    public static final String error_log = base_url + "api/InsertErrorLog";
    public static final String updateEmp_profile = base_url + "api/UpdateEmployeeProfile";


    /*---------------------Customer API--------------------------------------*/

    public static final String Customer_dashboard_details = base_url + "api/CustomerDashboard";
    public static final String Customer_update_details = base_url + "api/UpdateCustomerDetails";
    public static final String Customer_process = base_url + "api/GetCustomerProcess";
    public static final String Customer_residence = base_url + "api/ResidenceDetail";


    /*------------------------Global Varibales---------------------------------------------------*/

    public static final String TAG_Grant = "grant_type";
    public static final String TAG_token_type = "token_type";
    public static final String TAG_access_token = "access_token";
    public static final String TAG_username = "userName";
    public static final String TAG_name = "name";
    public static final String TAG_password = "password";

    public static final String TAG_aurtho = "Authorization";

    public static final String TAG_loginId = "loginid";
    public static final String TAG_companyId = "companyid";
    public static final String TAG_customerId = "customerid";
    public static final String TAG_empoyeeId = "employeeid";
    public static final String TAG_userType = "usertype";
    public static final String TAG_profilelogo = "logo";
    public static final String TAG_flaglogo = "flagurl";
    public static final String TAG_employeerole = "employeerole";
    public static final String TAG_dob = "dob";
    public static final String TAG_weburl = "weburl";

    public static final String TAG_status = "status";
    public static final String TAG_totalprocessesmonth = "totalprocessesmonth";
    public static final String TAG_totalsincestarted = "totalsincestarted";
    public static final String TAG_totalprocessesyear = "totalprocessesyear";
    public static final String TAG_openedprocesses = "openedprocesses";



    /*---------------for Residence List---------------------------------*/

    public static final String TAG_residentlist = "residentlist";
    public static final String TAG_res_id = "id";
    public static final String TAG_nickname = "nickname";
    public static final String TAG_street = "street";
    public static final String TAG_number = "number";
    public static final String TAG_apartment = "apartment";
    public static final String TAG_neighborhood = "neighborhood";
    public static final String TAG_city = "city";
    public static final String TAG_province = "province";
    public static final String TAG_place = "place";
    public static final String TAG_ready = "ready";
    public static final String TAG_usableplace = "usableplace";
    public static final String TAG_available = "available";
    public static final String TAG_residenceimages = "residenceimages";

    public static final String TAG_image = "image";
    public static final String TAG_res_img_id = "id";

    public static final String TAG_customer_residence = "residence";


    /*---------------for Process List-------------------------------------*/

    public static final String TAG_processmodel = "processmodel";
    public static final String TAG_doc_id = "id";
    public static final String TAG_process_id = "processid";
    public static final String TAG_residencename = "residencename";
    public static final String TAG_residence = "residence";
    public static final String TAG_documentlist = "documentlist";
    public static final String TAG_dateregistered = "dateregistered";
    public static final String TAG_customername = "customername";
    public static final String TAG_typename = "typename";
    public static final String TAG_situationname = "situationname";
    public static final String TAG_processnumber = "processnumber";
    public static final String TAG_processstatus = "processstatus";
    public static final String TAG_dateofclosure = "dateofclosure";
    public static final String TAG_notes = "notes";
    public static final String TAG_trackingnumber = "trackingnumber";
    public static final String TAG_interviewdate = "interviewdate";


    public static final String TAG_documentname = "documentname";
    public static final String TAG_updateddate = "updateddate";
    public static final String TAG_documentbinary = "documentbinary";
    public static final String TAG_mimetype = "mimetype";

    public static final String TAG_totalpayment = "totalpayment";
    public static final String TAG_amountremaining = "amountremaining";



    /*------------for Dashboard List------------------------------------------*/

    public static final String TAG_contactperson = "contactperson";
    public static final String TAG_telephone = "telephone";
    public static final String TAG_country = "country";
    public static final String TAG_paymentdate = "paymentdate";


    /*---------------for Cutomer List----------------------------------------*/

    public static final String TAG_customermodel = "customermodel";
    public static final String TAG_cus_id = "id";
    public static final String TAG_add = "address";
    public static final String TAG_email = "email";
    public static final String TAG_passport = "passport";
    public static final String TAG_profilePic = "profilepic";
    public static final String TAG_ancientname = "ancientname";
    public static final String TAG_airport = "arrivalairport";
    public static final String TAG_date = "arrivaldatetime";
    public static final String TAG_payment_status = "paymentstatus";
    public static final String TAG_company = "company";
    public static final String TAG_companyname = "companyname";
    public static final String TAG_currencyname = "currencyname";

    /*-----------------for TODO List------------------------------*/

    public static final String TAG_calendermodel = "calendermodel";
    public static final String TAG_todo_id = "id";
    public static final String TAG_todo_companyId = "company";
    public static final String TAG_todo = "todo";
    public static final String TAG_calenderdate = "calenderdate";
    public static final String TAG_todo_status = "status";
    public static final String TAG_description = "description";

    /*---------------------for notification-----------------------*/

    public static final String TAG_message = "message";
    public static final String TAG_msg_id = "id";
    public static final String TAG_msg_createdon = "createdon";
}
