import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import jdk.nashorn.api.scripting.JSObject;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static org.hamcrest.core.IsEqual.equalTo;

import static io.restassured.RestAssured.given;

public class TrelloTest {
    String propFileName = "config.properties";
    String host = "";

    private static String KEY = "b99c106d8546a2b3bb0574146d8ea1cb";
    private static String OAUTHSECRET = "a54d99148e4b8babc3c148f16e5bd66e6fc75c3c0b98e491dfd8e12b9ee40f95";
    private  static  String PERSONALTOKEN = "20eb5a9c05464df117c5b5b486ad79703d28c7f5685e1b4080d213322472cd32";
    private static String BoardId = "5d1ba8bf9a40bd0e942762d1";
    private static String ListID= "5d1ba8c00fdab220129a46ef";

    @Before
    public void setUp() throws IOException {
        Properties properties = new Properties();
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);
        properties.load(inputStream);
        host = properties.getProperty("host");
    }

    @Test
    public void getAllBoards(){
        given().auth().
                oauth(KEY, OAUTHSECRET, PERSONALTOKEN,"")
                .when().get(host+ "/1/members/me/boards").then().statusCode(200);
    }

    @Test
    public void createNewBoard() {
        String boardName = "TestBoard";
        given().auth()
                .oauth(KEY, OAUTHSECRET, PERSONALTOKEN, "")
                .given()
                .contentType(ContentType.JSON)
                .post(host + "/1/boards/?name="+ boardName)
                .then().statusCode(200)
                .and().body("name",equalTo(boardName));
    }

    @Test
    public void createNewList() {
        String boardName = "BoardWithList";
        String listName = "NewList";

        ValidatableResponse response = given().auth()
                .oauth(KEY, OAUTHSECRET, PERSONALTOKEN, "")
                .given()
                .contentType(ContentType.JSON)
                .post(host + "/1/boards/?name="+ boardName)
                .then().statusCode(200)
                .and().body("name",equalTo(boardName));

        JSONObject body = new JSONObject(response.extract().body().asString());
        String boardId = body.getString("id");

        given().auth()
                .oauth(KEY, OAUTHSECRET, PERSONALTOKEN, "")
                .when()
                .contentType(ContentType.JSON)
                .post(host + "/1/lists/?name="+listName+"&idBoard="+boardId)
                .then().statusCode(200)
                .and().body("name",equalTo(listName));

    }

    @Test
    public void createNewCard() {
        String boardName = "BoardWithList";
        String listName = "NewList";
        String CardName = "NewCard";

        ValidatableResponse response = given().auth()
                .oauth(KEY, OAUTHSECRET, PERSONALTOKEN, "")
                .given()
                .contentType(ContentType.JSON)
                .post(host + "/1/boards/?name="+ boardName)
                .then().statusCode(200)
                .and().body("name",equalTo(boardName));

        JSONObject body = new JSONObject(response.extract().body().asString());
        String boardId = body.getString("id");

        ValidatableResponse responseList = given().auth()
                .oauth(KEY, OAUTHSECRET, PERSONALTOKEN, "")
                .when()
                .contentType(ContentType.JSON)
                .post(host + "/1/lists/?name="+listName+"&idBoard="+boardId)
                .then().statusCode(200)
                .and().body("name",equalTo(listName));

        JSONObject bodyList = new JSONObject(responseList.extract().body().asString());
        String listId = bodyList.getString("id");

        given().auth()
                .oauth(KEY, OAUTHSECRET, PERSONALTOKEN, "")
                .when()
                .contentType(ContentType.JSON)
                .queryParam("idList", listId)
                .queryParam("name",CardName)
                .post(host + "/1/cards")
                .then().statusCode(200)
                .and().body("name",equalTo(CardName));

    }

    @Test
    public void ClosedCard() {
        String CardName = "NewCard";
        Boolean status = true;

        ValidatableResponse responseList = given().auth()
                .oauth(KEY, OAUTHSECRET, PERSONALTOKEN, "")
                .when()
                .contentType(ContentType.JSON)
                .get(host+ "/1/lists/"+ListID)
                .then().statusCode(200);

        JSONObject bodyList = new JSONObject(responseList.extract().body().asString());
        String listId = bodyList.getString("id");

        ValidatableResponse responseCard = given().auth()
                .oauth(KEY, OAUTHSECRET, PERSONALTOKEN, "")
                .when()
                .contentType(ContentType.JSON)
                .queryParam("idList", listId)
                .queryParam("name",CardName)
                .post(host + "/1/cards")
                .then().statusCode(200)
                .and().body("name",equalTo(CardName));

        JSONObject bodyCard = new JSONObject(responseCard.extract().body().asString());
        String cardId = bodyCard.getString("id");

        given().auth()
                .oauth(KEY, OAUTHSECRET, PERSONALTOKEN, "")
                .when()
                .contentType(ContentType.JSON)
                .queryParam("closed=", status)
                .put(host + "/1/cards/"+cardId)
                .then().statusCode(200);
                //.and().body("closed",equalTo(status));

    }

    @Test
    public void DeleteCard() {
        String CardName = "NewCard";
        Boolean status = true;

        ValidatableResponse responseList = given().auth()
                .oauth(KEY, OAUTHSECRET, PERSONALTOKEN, "")
                .when()
                .contentType(ContentType.JSON)
                .get(host+ "/1/lists/"+ListID)
                .then().statusCode(200);

        JSONObject bodyList = new JSONObject(responseList.extract().body().asString());
        String listId = bodyList.getString("id");

        ValidatableResponse responseCard = given().auth()
                .oauth(KEY, OAUTHSECRET, PERSONALTOKEN, "")
                .when()
                .contentType(ContentType.JSON)
                .queryParam("idList", listId)
                .queryParam("name",CardName)
                .post(host + "/1/cards")
                .then().statusCode(200)
                .and().body("name",equalTo(CardName));

        JSONObject bodyCard = new JSONObject(responseCard.extract().body().asString());
        String cardId = bodyCard.getString("id");

        given().auth()
                .oauth(KEY, OAUTHSECRET, PERSONALTOKEN, "")
                .when()
                .contentType(ContentType.JSON)
                .delete(host + "/1/cards/"+cardId)
                .then().statusCode(200);

    }
}
