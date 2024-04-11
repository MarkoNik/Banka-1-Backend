package rs.edu.raf.banka1.services.implementations;

import io.github.resilience4j.retry.Retry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import rs.edu.raf.banka1.dtos.market_service.ListingStockDto;
import rs.edu.raf.banka1.model.WorkingHoursStatus;
import rs.edu.raf.banka1.utils.JwtUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MarketServiceImplTest {

    private Retry serviceRetry;

    private RestTemplate marketServiceRestTemplate;

    private JwtUtil jwtUtil;

    private MarketServiceImpl marketService;

    @BeforeEach
    void setUp() {
        marketServiceRestTemplate = mock(RestTemplate.class);
        serviceRetry = mock(Retry.class);
        jwtUtil = mock(JwtUtil.class);
        marketService = new MarketServiceImpl(serviceRetry, marketServiceRestTemplate, jwtUtil);
    }

    @Test
    public void testGetAllListingsFromMarket_Successful() {
        String listType = "stock";
        ParameterizedTypeReference responseType = new ParameterizedTypeReference<>() {};
        List<Object> mockListings = new ArrayList<>();
        mockListings.add(new ListingStockDto());
        mockListings.add(new ListingStockDto());
        when(marketServiceRestTemplate.exchange(
                eq("market/listing/get/" + listType),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                any(ParameterizedTypeReference.class)
        )).thenReturn(new ResponseEntity<>(mockListings, HttpStatus.OK));

        List<Object> result = marketService.getAllListingsFromMarket(listType);

        assertEquals(mockListings, result);
    }

    @Test
    public void testGetAllListingsFromMarket_NotFound() {
        String listType = "stock";
        ParameterizedTypeReference<List<Object>> responseType = new ParameterizedTypeReference<>() {};
        when(marketServiceRestTemplate.exchange(
                eq("market/listing/get/" + listType),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(responseType)
        )).thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        List<Object> result = marketService.getAllListingsFromMarket(listType);

        assertEquals(new ArrayList<>(), result);
    }

    @Test
    public void testGetAllListingsFromMarket_BadRequest() {
        String listType = "stock";
        ParameterizedTypeReference<List<Object>> responseType = new ParameterizedTypeReference<>() {};
        when(marketServiceRestTemplate.exchange(
                eq("market/listing/get/" + listType),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(responseType)
        )).thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));

        List<Object> result = marketService.getAllListingsFromMarket(listType);

        assertEquals(new ArrayList<>(), result);
    }

/////////////////////////////////////////

    @Test
    public void testGetStockByIdFromMarket_Successful() {
        Long stockId = 123L;
        ListingStockDto mockStockDto = new ListingStockDto();
        when(marketServiceRestTemplate.exchange(
                eq("market/listing/stock/" + stockId),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(ListingStockDto.class)
        )).thenReturn(new ResponseEntity<>(mockStockDto, HttpStatus.OK));

        ListingStockDto result = marketService.getStockByIdFromMarket(stockId);

        assertEquals(mockStockDto, result);
    }

    @Test
    public void testGetStockByIdFromMarket_NotFound() {
        Long stockId = 456L;
        when(marketServiceRestTemplate.exchange(
                eq("market/listing/stock/" + stockId),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(ListingStockDto.class)
        )).thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        ListingStockDto result = marketService.getStockByIdFromMarket(stockId);

        assertNull(result);
    }

    @Test
    public void testGetStockByIdFromMarket_BadRequest() {
        Long stockId = 789L;
        when(marketServiceRestTemplate.exchange(
                eq("market/listing/stock/" + stockId),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(ListingStockDto.class)
        )).thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));

        ListingStockDto result = marketService.getStockByIdFromMarket(stockId);

        assertNull(result);
    }
////////////////////////////////////////////////////////////
    @Test
    public void testGetAllListings_Successful() {
        List<Object> mockStockList = Arrays.asList(new ListingStockDto(), new ListingStockDto());
        when(marketServiceRestTemplate.exchange(
                eq("market/listing/get/stock"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                any(ParameterizedTypeReference.class)
        )).thenReturn(new ResponseEntity<>(mockStockList, HttpStatus.OK));

        List<ListingStockDto> result = marketService.getAllStocks();

        assertEquals(2, result.size());
    }

    @Test
    public void testGetAllListings_UnsuccessfulResponse() {
        when(marketServiceRestTemplate.exchange(
                eq("market/listing/get/stock"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                any(ParameterizedTypeReference.class)
        )).thenReturn(new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR));

        List<ListingStockDto> result = marketService.getAllStocks();

        assertEquals(0, result.size());
    }

    @Test
    public void testGetAllListings_NotFound() {
        when(marketServiceRestTemplate.exchange(
                eq("market/listing/get/stock"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                any(ParameterizedTypeReference.class)
        )).thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        List<ListingStockDto> result = marketService.getAllStocks();

        assertEquals(0, result.size());
    }

    @Test
    public void testGetAllListings_BadRequest() {
        when(marketServiceRestTemplate.exchange(
                eq("market/listing/get/stock"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                any(ParameterizedTypeReference.class)
        )).thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));

        List<ListingStockDto> result = marketService.getAllStocks();

        assertEquals(0, result.size());
    }
  /////////////////////////////////////////////////////////////
    @Test
    public void testGetWorkingHoursForStock_Successful() {
        Long stockId = 123L;

        ResponseEntity<String> successResponse = new ResponseEntity<>("OPENED", HttpStatus.OK);
        when(marketServiceRestTemplate.exchange(
                eq("market/exchange/stock/" + stockId + "/time"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(String.class)
        )).thenReturn(successResponse);

        WorkingHoursStatus result = this.marketService.getWorkingHoursForStock(stockId);

        assertEquals(WorkingHoursStatus.OPENED, result);
    }

    @Test
    public void testGetWorkingHoursForStock_NotFound() {
        Long stockId = 456L;
        when(marketServiceRestTemplate.exchange(
                eq("market/exchange/stock/" + stockId + "/time"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(String.class)
        )).thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        WorkingHoursStatus result = this.marketService.getWorkingHoursForStock(stockId);

        assertNull(result);
    }

    @Test
    public void testGetWorkingHoursForStock_BadRequest() {
        Long stockId = 789L;
        when(marketServiceRestTemplate.exchange(
                eq("market/exchange/stock/" + stockId + "/time"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(String.class)
        )).thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));

        WorkingHoursStatus result = marketService.getWorkingHoursForStock(stockId);
        assertNull(result);
    }

}