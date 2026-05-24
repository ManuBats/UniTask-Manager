package com.example.unitask_manager.data.repository;

import com.example.unitask_manager.data.local.TokenManager;
import com.example.unitask_manager.database.DatabaseHelper;
import com.example.unitask_manager.dto.response.StatsResponse;
import com.example.unitask_manager.network.ApiClient;
import com.example.unitask_manager.network.ApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StatsRepository {

    private final ApiService apiService;
    private final DatabaseHelper dbHelper;

    public StatsRepository(DatabaseHelper dbHelper, TokenManager tokenManager) {
        this.apiService = ApiClient.getApiService(tokenManager);
        this.dbHelper = dbHelper;
    }

    public void getStats(final StatsCallback callback) {
        apiService.getDashboardStats().enqueue(new Callback<StatsResponse>() {
            @Override
            public void onResponse(Call<StatsResponse> call, Response<StatsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    fallbackToLocal(callback);
                }
            }

            @Override
            public void onFailure(Call<StatsResponse> call, Throwable t) {
                fallbackToLocal(callback);
            }
        });
    }

    private void fallbackToLocal(StatsCallback callback) {
        StatsResponse stats = new StatsResponse();
        stats.setTotalActividades(dbHelper.contarCompletadas() + dbHelper.contarPendientes());
        stats.setCompletadas(dbHelper.contarCompletadas());
        stats.setPendientes(dbHelper.contarPendientes());
        callback.onSuccess(stats);
    }

    public interface StatsCallback {
        void onSuccess(StatsResponse stats);
        void onError(String error);
    }
}
