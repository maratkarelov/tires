package education.karelov.tires2;

import java.util.List;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;

public interface APIClient {
	@POST("/register")
	void register(@Body PostJsonForPushNotification s, Callback<ResponseForPushNotification> cb);

	@GET("/yearList")
	Respond getYears();

	@GET("/makeList")
	Respond getMakers(@Query("year") String year);

	@GET("/modelList")
	Respond getModels(@Query("make") String make);
	
	@GET("/subModelList")
	Respond getSubModels(@Query("model") String model);

	@GET("/findResult")
	TireInfo getInfo(@Query("baseId") String baseId);
}