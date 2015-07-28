package education.karelov.tires2;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;

public interface APIClient {
	@POST("/register")
	void register(@Body PostJsonForPushNotification s, Callback<ResponseForPushNotification> cb);

	@GET("/yearList")
	void getYears(Callback<Respond> cb);

	@GET("/makeList")
	void getMakers(@Query("year") String year, Callback<Respond> cb);

	@GET("/modelList")
	void getModels(@Query("make") String make, Callback<Respond> cb);
	
	@GET("/subModelList")
	void getSubModels(@Query("model") String model, Callback<Respond> cb);

	@GET("/findResult")
	void getInfo(@Query("baseId") String baseId, Callback<TireInfo> cb);
}