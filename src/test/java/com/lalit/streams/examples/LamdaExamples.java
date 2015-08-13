package com.lalit.streams.examples;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.reducing;
import static java.util.stream.Collectors.summingLong;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.Test;
import org.springframework.format.datetime.DateFormatter;
/**
 * Examples of composing functionals on streams.
 * 
 * The following tests load data from a csv file and create  
 * data pipelines for below mentioned cases
 * 
 *  Case 1 - List of Products with total sale amounts of each
 *  Case 2 - Classification of Products with respective sale items
 *  Case 3 - Total sale in particular month (e.g. december 2014)
 *  Case 4 - Daywise total sale
 *  Case 5 - Product with max collective sales
 *  Case 6 - Monthwise sale distribution of products
 *  Case 7 - Highest revenue grosssing month  
 * 
 * 
 * @author lalit_ruchandani
 *
 */
public class LamdaExamples {
	
	private final List<ProductSale> productSaleItems;
	
	public LamdaExamples(){
		try {
			productSaleItems = new CSVLoader().loadProductSaleItems();
		} catch (IOException | URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}
	

	/**
	 * Case 1 - List of all product and their respective total sale amount
	 * 
	 * @param productSaleItems
	 */
	@Test
	public void productAndTheirRespectiveSaleAmount() {

		Map<String, Long> productSales = productSaleItems.stream().collect(
				groupingBy(ProductSale::getProductName, summingLong((ProductSale e) -> e.getSaleAmount().longValue())));

		assertEquals(90L, productSales.get("Product1").longValue());
	}

	/**
	 * Case 2 - Map of all product with the list of their sale
	 * 
	 * @param productSaleItems
	 */
	@Test
	public void productsWithAssociatedSaleItems() {
		Map<String, List<ProductSale>> productSales = productSaleItems.stream().collect(
				groupingBy(ProductSale::getProductName));

		assertEquals(4, productSales.get("Product1").size());
	}

	/**
	 * Case 3 - Total sale in month of december 2014
	 * 
	 * @param productSaleItems
	 */
	@Test
	public void totalSaleInMonthOfDecember() {
		BigDecimal totalSaleInDecember = productSaleItems.stream().filter(e -> isInRange(e, Calendar.DECEMBER))
				.map(e -> e.getSaleAmount()).reduce(BigDecimal.ZERO, (s, e) -> s.add(e));

		assertEquals(1130L, totalSaleInDecember.longValue());
	}


	/**
	 * Case 4 - Daywise total sale
	 * 
	 * @param productSaleItems
	 */
	 @Test
	 public void daywiseTotalSale() {
		Map<Date, BigDecimal> daywiseTotal = productSaleItems.stream().collect(
				groupingBy(ProductSale::getSaleDate,
						reducing(BigDecimal.ZERO, (ProductSale e) -> e.getSaleAmount(), (s, elt) -> s.add(elt))));
		assertEquals(110L,
				daywiseTotal.get(new Calendar.Builder().setDate(2014, Calendar.DECEMBER, 12).build().getTime())
						.longValue());
	}
	
	/**
	 * 
	 * Case 5 - Top Selling  product (overall)
	 * @param productSaleItems
	 */
	@Test
	 public void productWithMaxSale() {

		Map<String, BigDecimal> productWiseTotal = productSaleItems.stream().collect(
				groupingBy(ProductSale::getProductName,
						reducing(BigDecimal.ZERO, (ProductSale e) -> e.getSaleAmount(), (s, elt) -> s.add(elt))));

		Optional<Entry<String, BigDecimal>> productWithMaxSale = productWiseTotal.entrySet().stream()
				.max((s, e) -> s.getValue().compareTo(e.getValue()));

		assertEquals(true, productWithMaxSale.isPresent());
		assertEquals(240L, productWithMaxSale.get().getValue().longValue());		
	}

	/**
	 * Case 6 -  Monthwise sale distribution of products
	 */
	@Test
	public void topSellingProductMonthWise() {

		Map<Calendar, Map<String,BigDecimal>> monthWiseSummary = productSaleItems.stream().collect(
				groupingBy(this::mapToMonth,
						groupingBy(ProductSale::getProductName,
									reducing(BigDecimal.ZERO, (ProductSale e) -> e.getSaleAmount(),
											(s,elt)->s.add(elt)))));
		
		Calendar monthOfDecember = getDate(0,11,2014);
		assertEquals(10L, monthWiseSummary.get(monthOfDecember).size());
	}
	
	/**
	 * Case 7 -  Highest revenue grossing month
	 */
	@Test
	 public void highestRevenueGrossingMonths() {

		Map<Calendar, BigDecimal> monthWiseSummary = productSaleItems.stream().collect(
				groupingBy(this::mapToMonth,
									reducing(BigDecimal.ZERO, (ProductSale e) -> e.getSaleAmount(),
											(s,elt)->s.add(elt))));
		Entry<Calendar,BigDecimal> highestRevenue = monthWiseSummary.entrySet().stream().max((s1,s2) -> s1.getValue().compareTo(s2.getValue())).get();
		assertEquals(1130L, highestRevenue.getValue().longValue());
		assertEquals(getDate(0, 11, 2014), highestRevenue.getKey());
		
	}


	boolean isInRange(ProductSale productSale, int calenderMonth) {
		Calendar saleDate = new Calendar.Builder().setInstant(productSale.getSaleDate()).build();
		return saleDate.get(Calendar.MONTH) == calenderMonth;
	}
	 
	Calendar mapToMonth(ProductSale productSale){
		Calendar saleDate = new Calendar.Builder().setInstant(productSale.getSaleDate()).build();
		Calendar monthOfSale = new Calendar.Builder().set(Calendar.MONTH, saleDate.get(Calendar.MONTH))
													 .set(Calendar.YEAR, saleDate.get(Calendar.YEAR))
													 .build();
		return monthOfSale;
	}
	
	Calendar getDate(int day,int month,int year){
		Calendar.Builder requesteddDate = new Calendar.Builder();
		if(day!=0){
			requesteddDate.set(Calendar.DAY_OF_MONTH, day);
		}
		if(month!=0){
			requesteddDate.set(Calendar.MONTH, month);
		}
		if(year!=0){
			requesteddDate.set(Calendar.YEAR, year);
		}
		
		return requesteddDate.build();
	}
	
}

/**
 * Very Basic CSV loader usbing Java 8 lamdas and a little ugly hard coded
 * coverter
 * 
 * @author lalit_ruchandani
 *
 */
class CSVLoader {

	/**
	 * Mapper function to be used in lamda's
	 * 
	 * @param line
	 * @param delimiter
	 * @return
	 */
	ProductSale convertProductSale(String line, String delimiter) {
		final String[] s = line.split(delimiter);
		final ProductSale productSale = new ProductSale();
		productSale.setProductName(s[0]);
		productSale.setSaleAmount(new BigDecimal(s[3]));
		productSale.setSaleId(Integer.valueOf(s[1]));
		try {
			productSale.setSaleDate(new DateFormatter("yyyy-MM-dd HH:mm:ss").parse(s[2].replace("\"", ""),
					Locale.getDefault()));
		} catch (final ParseException e) {
			e.printStackTrace();
		}
		return productSale;
	}

	public List<ProductSale> loadProductSaleItems() throws IOException, URISyntaxException {
		final List<ProductSale> productSaleItems = Files
				.lines(Paths.get(this.getClass().getResource("product_sale.csv").toURI())).skip(1)
				.map(line -> this.convertProductSale(line, ",")).collect(Collectors.toList());
		return productSaleItems;
	}
}