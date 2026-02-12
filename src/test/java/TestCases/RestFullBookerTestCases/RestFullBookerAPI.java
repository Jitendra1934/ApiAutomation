package TestCases.RestFullBookerTestCases;

import PojoClases.CreateBookingPojoClasses.Booking;
import PojoClases.CreateBookingPojoClasses.BookingDates;
import PojoClases.Token.TokenPojo;
import Utilities.FileReader;
import com.google.gson.Gson;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.List;

import static org.hamcrest.Matchers.notNullValue;


public class RestFullBookerAPI {

    RequestSpecification rs;
    Response r;
    ValidatableResponse vr;
    public static int bookingid;
    public String authToken() throws IOException {

        TokenPojo p = new TokenPojo();
        p.setUsername(FileReader.readProperty("username"));
        p.setPassword(FileReader.readProperty("password"));
        Gson g = new Gson();
        String payLoad = g.toJson(p);
        System.out.println(payLoad);
        rs = RestAssured.given();
        rs.baseUri(FileReader.readProperty("baseuri"));
        rs.basePath("/auth");
        rs.header("Content-Type","application/json");
        rs.body(payLoad);
        r = rs.when().post();
        vr = r.then();
        vr.statusCode(200);
        String accessToken = r.jsonPath().getString("token");
        System.out.println("Accestoken :"+accessToken);
        return accessToken;

    }

    @Test(priority = 1)
    public void getBooking() throws IOException {
        rs=RestAssured.given();
        rs.baseUri(FileReader.readProperty("baseuri"));
        rs.basePath("/booking");
        r = rs.when().get();
        vr = r.then().statusCode(200);
        List<Integer> id = r.jsonPath().getList("bookingid");
        Assert.assertTrue(id.size()>0);

    }

    @Test(dependsOnMethods = "createBooking")
    public void getBookingById() throws IOException {
        rs=RestAssured.given();
        rs.baseUri(FileReader.readProperty("baseuri"));
        rs.basePath("/booking"+"/"+bookingid);
        rs.log().all();
        r = rs.when().get();
        vr = r.then().statusCode(200).assertThat().body("firstname", notNullValue());
        vr.log().all();
        //r.prettyPrint();
        String firstName = r.jsonPath().getString("firstname");
        Assert.assertNotNull(firstName);
       // Assert.assertEquals(firstName, "Ajay");
        System.out.println(firstName);
    }

    @Test(priority = 2)
    public void createBooking() throws IOException {

       /* {
    "firstname" : "Jim",
    "lastname" : "Brown",
    "totalprice" : 111,
    "depositpaid" : true,
    "bookingdates" : {
        "checkin" : "2018-01-01",
        "checkout" : "2019-01-01"
    },
    "additionalneeds" : "Breakfast"
}'*/

        BookingDates dates = new BookingDates();
        dates.setCheckin("2018-01-01");
        dates.setCheckout("2019-01-01");

        Booking payload = new Booking();
        payload.setFirstname("jitendra");
        payload.setLastname("punnam");
        payload.setTotalprice(123);
        payload.setDepositpaid(true);
        payload.setBookingdates(dates);
        payload.setAdditionalneeds("breakFast");


        rs =RestAssured.given();
        rs.baseUri(FileReader.readProperty("baseuri"));
        rs.basePath("/booking");
        rs.contentType(ContentType.JSON);
        rs.body(payload);
        rs.log().all();
        r = rs.post();
        vr = r.then().statusCode(200).assertThat().body("booking.firstname", notNullValue());
        vr.log().all();
        String firstname = r.jsonPath().getString("booking.firstname");
        bookingid = r.jsonPath().getInt("bookingid");
        System.out.println(bookingid);
        Assert.assertEquals(firstname,"jitendra");

    }

    @Test(priority = 3, dependsOnMethods = "createBooking")
    public void updateBooking() throws IOException {

        BookingDates dates = new BookingDates();
        dates.setCheckin("2018-01-02");
        dates.setCheckout("2019-01-02");

        Booking payload = new Booking();
        payload.setFirstname("Ajay");
        payload.setLastname("Goud");
        payload.setTotalprice(158);
        payload.setDepositpaid(true);
        payload.setBookingdates(dates);
        payload.setAdditionalneeds("Pasta");

        String token = authToken();

        rs = RestAssured.given();

        rs.baseUri(FileReader.readProperty("baseuri"));
        rs.basePath("/booking/" + bookingid);

        rs.header("Content-Type", "application/json");
        rs.header("Accept", "application/json");
        rs.header("Cookie", "token=" + token);


        rs.body(payload);
        rs.log().all();

        r = rs.put();

        vr = r.then().statusCode(200).assertThat().body("firstname", notNullValue());

        String firstname = r.jsonPath().getString("firstname");
        Assert.assertEquals(firstname, "Ajay");
    }

}
