package Shared.Server.Responses;

import Shared.Server.Model.SuperModel;

import java.util.ArrayList;

public class DataResponse extends Response {
    private ArrayList<SuperModel> data;

    /**
     * Constructs the array associated with the data message. Used in /person and /event.
     * @param data
     */
    public DataResponse(ArrayList<SuperModel> data) {
        this.data = data;
    }

    public ArrayList<SuperModel> getData() {
        return data;
    }

    public void setData(ArrayList<SuperModel> data) {
        this.data = data;
    }
}
