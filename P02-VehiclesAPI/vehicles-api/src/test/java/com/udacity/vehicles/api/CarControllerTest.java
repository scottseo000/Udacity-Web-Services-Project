package com.udacity.vehicles.api;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import com.udacity.vehicles.client.maps.MapsClient;
import com.udacity.vehicles.client.prices.PriceClient;
import com.udacity.vehicles.domain.Condition;
import com.udacity.vehicles.domain.Location;
import com.udacity.vehicles.domain.car.Car;
import com.udacity.vehicles.domain.car.Details;
import com.udacity.vehicles.domain.manufacturer.Manufacturer;
import com.udacity.vehicles.service.CarService;
import java.net.URI;
import java.util.Collections;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Implements testing of the CarController class.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
public class CarControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private JacksonTester<Car> json;

    @MockBean
    private CarService carService;

    @MockBean
    private PriceClient priceClient;

    @MockBean
    private MapsClient mapsClient;

    /**
     * Creates pre-requisites for testing, such as an example car.
     */
    @Before
    public void setup() {
        Car car = getCar();
        car.setId(1L);
        given(carService.save(any())).willReturn(car);
        given(carService.findById(any())).willReturn(car);
        given(carService.list()).willReturn(Collections.singletonList(car));
    }

    /**
     * Tests for successful creation of new car in the system
     * @throws Exception when car creation fails in the system
     */
    @Test
    public void createCar() throws Exception {
        Car car = getCar();
        mvc.perform(
                post(new URI("/cars"))
                        .content(json.write(car).getJson())
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isCreated());
    }

    /**
     * Tests if the read operation appropriately returns a list of vehicles.
     * @throws Exception if the read operation of the vehicle list fails
     */
    @Test
    public void listCars() throws Exception {
        mvc.perform(get(new URI("/cars")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.carList").exists())
                //There should only be 1 car in the list, the one created in getCar()
                //This one should return same as findCar but inside {"_embedded":{"carList":[ **the entire json of car id 1L** ]}
                .andExpect(content().string(
                        "{\"_embedded\":{\"carList\":[" +
                                "{\"id\":1," +
                                "\"createdAt\":null," +
                                "\"modifiedAt\":null," +
                                "\"condition\":\"USED\"," +
                                "\"details\":{" +
                                "\"body\":\"sedan\"," +
                                "\"model\":\"Impala\"," +
                                "\"manufacturer\":{\"code\":101,\"name\":\"Chevrolet\"}," +
                                "\"numberOfDoors\":4," +
                                "\"fuelType\":" +
                                "\"Gasoline\"," +
                                "\"engine\":\"3.6L V6\"," +
                                "\"mileage\":32280," +
                                "\"modelYear\":2018," +
                                "\"productionYear\":2018," +
                                "\"externalColor\":\"white\"}," +

                                "\"location\":{" +
                                "\"lat\":40.73061," +
                                "\"lon\":-73.935242," +
                                "\"address\":null," +
                                "\"city\":null," +
                                "\"state\":null," +
                                "\"zip\":null}," +
                                "\"price\":null," +
                                "\"_links\":{\"self\":{\"href\":\"http://localhost/cars/1\"}," +
                                "\"cars\":{\"href\":\"http://localhost/cars\"}}}]}," +
                                "\"_links\":{\"self\":{\"href\":\"http://localhost/cars\"}}}"))
                .andDo(print());
    }

    /**
     * Tests the read operation for a single car by ID.
     * @throws Exception if the read operation for a single car fails
     */
    @Test
    public void findCar() throws Exception {
        /*
        mvc.perform(get("/cars/{id}", car.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(car.getId()))
                .andDo(print());
         */
        //Gonna brute force it instead
        mvc.perform(get("/cars/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().string(
                        "{\"id\":1," +
                        "\"createdAt\":null," +
                        "\"modifiedAt\":null," +
                        "\"condition\":\"USED\"," +
                        "\"details\":{" +
                        "\"body\":\"sedan\"," +
                        "\"model\":\"Impala\"," +
                        "\"manufacturer\":{\"code\":101,\"name\":\"Chevrolet\"}," +
                        "\"numberOfDoors\":4," +
                        "\"fuelType\":" +
                        "\"Gasoline\"," +
                        "\"engine\":\"3.6L V6\"," +
                        "\"mileage\":32280," +
                        "\"modelYear\":2018," +
                        "\"productionYear\":2018," +
                        "\"externalColor\":\"white\"}," +

                        "\"location\":{" +
                        "\"lat\":40.73061," +
                        "\"lon\":-73.935242," +
                        "\"address\":null," +
                        "\"city\":null," +
                        "\"state\":null," +
                        "\"zip\":null}," +
                        "\"price\":null," +
                        "\"_links\":{\"self\":{\"href\":\"http://localhost/cars/1\"}," +
                        "\"cars\":{\"href\":\"http://localhost/cars\"}}}"));

    }

    /**
     * Tests the deletion of a single car by ID.
     * @throws Exception if the delete operation of a vehicle fails
     */
    @Test
    public void deleteCar() throws Exception {
        mvc.perform(delete("/cars/{id}", 1L))
                .andExpect(status().isNoContent());
    }
    /**
     * Tests the update operation for a single car by ID.
     * @throws Exception if the read operation for a single car fails
     */
    @Test
    public void updateCar() throws Exception {
        Car car = getCar();

        //id shows up as null after the update while all other details were properly updated or transferred
        car.setId(1L);

        //Details constructor does not take parameters, so it must be created then updated
        Details details = car.getDetails();
        details.setFuelType("Electric");
        details.setManufacturer(new Manufacturer(200, "BMW"));
        details.setModel("i5");
        car.setDetails(details);
        given(carService.save(any())).willReturn(car);

        mvc.perform(put("/cars/{id}", 1L)
                        .content(json.write(car).getJson())
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(content().string(
                        "{\"id\":1," +
                                "\"createdAt\":null," +
                                "\"modifiedAt\":null," +
                                "\"condition\":\"USED\"," +
                                "\"details\":{" +
                                "\"body\":\"sedan\"," +
                                "\"model\":\"i5\"," +
                                "\"manufacturer\":{\"code\":200,\"name\":\"BMW\"}," +
                                "\"numberOfDoors\":4," +
                                "\"fuelType\":" +
                                "\"Electric\"," +
                                "\"engine\":\"3.6L V6\"," +
                                "\"mileage\":32280," +
                                "\"modelYear\":2018," +
                                "\"productionYear\":2018," +
                                "\"externalColor\":\"white\"}," +

                                "\"location\":{" +
                                "\"lat\":40.73061," +
                                "\"lon\":-73.935242," +
                                "\"address\":null," +
                                "\"city\":null," +
                                "\"state\":null," +
                                "\"zip\":null}," +
                                "\"price\":null," +
                                "\"_links\":{\"self\":{\"href\":\"http://localhost/cars/1\"}," +
                                "\"cars\":{\"href\":\"http://localhost/cars\"}}}"));
    }
    /**
     * Creates an example Car object for use in testing.
     * @return an example Car object
     */
    private Car getCar() {
        Car car = new Car();
        car.setLocation(new Location(40.730610, -73.935242));
        Details details = new Details();
        Manufacturer manufacturer = new Manufacturer(101, "Chevrolet");
        details.setManufacturer(manufacturer);
        details.setModel("Impala");
        details.setMileage(32280);
        details.setExternalColor("white");
        details.setBody("sedan");
        details.setEngine("3.6L V6");
        details.setFuelType("Gasoline");
        details.setModelYear(2018);
        details.setProductionYear(2018);
        details.setNumberOfDoors(4);
        car.setDetails(details);
        car.setCondition(Condition.USED);
        return car;
    }
}