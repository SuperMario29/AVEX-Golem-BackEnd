package avex.golem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.stripe.Stripe;
import com.stripe.exception.APIConnectionException;
import com.stripe.exception.APIException;
import com.stripe.exception.AuthenticationException;
import com.stripe.exception.CardException;
import com.stripe.exception.InvalidRequestException;
import com.stripe.model.Customer;

import avex.models.Order;

public class OrderMaintenance {

	private AVEXDB avexDB = new AVEXDB();
	private PaymentsApi paymentsapi = new PaymentsApi();

	public void OrderUpKeep() {
		try{
			List<BasicDBObject> orderList = avexDB.GetOrders();
			if (orderList != null && orderList.size() > 0) {
				List<Order> orders = new ArrayList<>();
				for (BasicDBObject order : orderList) {
					try{
						System.out.println("Received Order : " + order);
						Order o = new Order();
						o.setAthleteID(order.getString("athleteid"));
						o.setCustomerID(order.getString("customerid"));
						o.setPrice(order.getDouble("price"));
						o.setQuantity(order.getInt("quantity"));
						o.setActiontype(order.getString("actiontype"));
						o.setRecordStatus(order.getInt("recordstatus"));
						o.setRecordstatusdate(order.getDate("recordstatusdate"));
						if(order.get("ispending") != null && order.getBoolean("ispending")  == true){
							o.setIspending(true);
						}
						else{
							o.setIspending(false);
						}
						System.out.println("Added Order To List: " + o);
						orders.add(o);
					}
					catch(Exception ex){
						System.out.println("Exception: " + ex.getMessage());
						System.out.println("Stack Trace: " + ex.getStackTrace());
					}
				}
				ProcessPendingOrders(orders);
			}
		}
		catch(Exception ex){
			System.out.println("Exception: " + ex.getLocalizedMessage());
		}
	}

	@SuppressWarnings("unused")
	private void ProcessPendingOrders(List<Order> orderList) {
		try{
		Collections.sort(orderList);
		for (Order order : orderList) {
			if (order.getIspending()) {

				System.out.println("Order Object: " + order);
				
				double finalcommission = 0.00;
				double finalprice = 0.00;
				int availableshares = 0;
				int totalshares = 0;
				String athleteid = "";
				String athletequote = "";
				String athletename = "";
				String athleteimageurl = "";
				int externalathleteid = 0;
				boolean isvalidorder = false;
				boolean isresellable = false;
				int remainingshares = 0;
				int updateshares = 0;
				List<BasicDBObject> customerhistory = new ArrayList<>();

				BasicDBObject existingposition = new BasicDBObject();
				BasicDBObject customer = avexDB.GetUser(order.getCustomerID());
				System.out.println("Customer Object: " + customer);
				BasicDBObject settings = avexDB.GetSettings();
				System.out.println("Settings Object: " + settings);
				BasicDBObject athlete = avexDB.GetAthleteByID(order.getAthleteID());
				System.out.println("Athlete Object: " + athlete);
				BasicDBObject customerHistory = new BasicDBObject();

				if (customer != null && !customer.isEmpty() && settings != null && !settings.isEmpty()
						&& athlete != null && !athlete.isEmpty()) {
					Stripe.apiKey = "sk_test_7itp1Ch8S4JWgslVvG5qknUN";
					Customer stripecustomer;
					try {
						stripecustomer = Customer.retrieve(customer.getString("stripeaccount"));
						double commission = settings.getDouble("commission");
						if (stripecustomer != null) {
							double accountbalance = (stripecustomer.getAccountBalance() * -1) / 100;
							finalprice = athlete.getDouble("currentprice");
							availableshares = athlete.getInt("availableshares");
							totalshares = athlete.getInt("totalshares");
							externalathleteid = athlete.getInt("athleteid");
							athletequote = athlete.getString("quote");
							athletename = athlete.getString("name");
							athleteimageurl = athlete.getString("imageurl");
							isresellable = athlete.getBoolean("isresellable");
							athleteid = athlete.getString("_id");
							updateshares = order.getQuantity();
							long userqueue = avexDB.GetUserQueuePosition(athleteid);
							BasicDBList listofathletes = (BasicDBList) customer.get("listofathletes");

							while (avexDB.CurrentQueue(userqueue, athleteid)) {
								try {
									Thread.sleep(1000);
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}

							for (Object position : listofathletes) {
								BasicDBObject custposition = (BasicDBObject) position;
								if (custposition.get("athleteid") == athleteid) {
									existingposition = custposition;
									System.out.println("Found Athlete! Name:  " + existingposition);
								}
							}

							if (order.getActiontype().toLowerCase() == "buy") {
								if (availableshares > order.getQuantity() && accountbalance != 0.00) {
									List<BasicDBObject> orders = avexDB.GetOrdersByAthleteId(1, athleteid);

									if (orders != null && !orders.isEmpty()) {
										int initialQuantityRequest = updateshares;
										int remainingShares = 0;

										for (BasicDBObject nextorder : orders) {
											if (updateshares <= 0) {
												break;
											}
											if (nextorder != null) {
												int nextorderquantity = nextorder.getInt("quantity");
												int newquantity = nextorderquantity - updateshares;
												int tempQuantity = updateshares;
												String nextcustomerid = nextorder.getString("customerid");

												if (nextcustomerid != null) {
													if (newquantity < 0) {
														nextorder.replace("quantity", 0);
														updateshares = newquantity;
														nextorder.replace("recordstatus", 3);
													} else {
														nextorder.replace("quantity", newquantity);
														updateshares = 0;
													}

													BasicDBObject nextuser = avexDB.GetUser(nextcustomerid);

													if (nextuser != null) {
														Customer nextcustomer = paymentsapi.GetCustomerBalance(
																nextuser.getString("stripeaccount"));

														if (nextcustomer.getAccountBalance() != null) {
															int nextuseraccountbalance = (nextcustomer
																	.getAccountBalance() * -1) / 100;

															BasicDBList nextuserlistofathletes = (BasicDBList) nextuser
																	.get("listofathletes");
															BasicDBObject nextposition = new BasicDBObject();

															for (Object position : nextuserlistofathletes) {
																BasicDBObject custposition = (BasicDBObject) position;
																if (custposition.get("athleteid") == athleteid) {
																	nextposition = custposition;
																	System.out.println("Found Athlete! Name:  "
																			+ existingposition);
																}
															}

															if (nextposition != null
																	&& nextposition.getInt("recordstatus") != 3) {
																int nextcurrentquantity = nextposition
																		.getInt("quantity");
																double nextcurrentcostpershare = nextposition
																		.getDouble("costpershare");
																int nextrecordstatus = 2;

																double ordercost = tempQuantity * finalprice;
																newquantity = +nextcurrentquantity - +tempQuantity;

																if (newquantity == 0) {
																	nextrecordstatus = 3;
																} else if (newquantity < 0) {
																	continue;
																}

																nextposition.replace("quantity", newquantity);
																nextposition.replace("costpershare",
																		((+nextcurrentquantity
																				* +nextcurrentcostpershare)
																				- (+ordercost)) / +newquantity);
																nextposition.replace("athletename",
																		athlete.get("name"));
																nextposition.replace("imageurl",
																		athlete.getString("imageurl"));
																nextposition.replace("recordstatusdate", new Date());
																nextposition.replace("recordstatus", nextrecordstatus);

																Double nextuservalue = ((+nextuseraccountbalance
																		+ +ordercost) * 100) * -1;
																int nextusernewbalance = nextuservalue.intValue();

																if (paymentsapi.UpdateCustomerBalance(
																		nextuser.getString("stripeaccount"),
																		nextusernewbalance)) {

																	for (Object position : nextuserlistofathletes) {
																		BasicDBObject custposition = (BasicDBObject) position;
																		if (custposition
																				.get("athleteid") == athleteid) {
																			custposition = nextposition;
																			System.out.println(
																					"Update Customer Position For Athlete! Name:  "
																							+ existingposition);
																		}
																	}

																	nextuser.replace("listofathletes",
																			nextuserlistofathletes);
																	avexDB.SaveUser(nextuser);
																}
															}
														}
													} else {

													}
												} else {
													if (newquantity < 0) {
														nextorder.replace("quantity", 0);
														updateshares = newquantity;
														nextorder.replace("recordstatus", 3);
														nextorder.replace("isresellable", true);
													} else {
														nextorder.replace("quantity", newquantity);
														updateshares = 0;
													}
												}
											}
										}

										// Process Remaining Order
										if (updateshares >= 0) {
											remainingshares = updateshares;
											updateshares = +initialQuantityRequest - +updateshares;
											order.setCost(+updateshares * +finalprice);
											order.setQuantity(updateshares);

											BasicDBObject leftoverorder = new BasicDBObject().append("quote",
													athletequote);
											leftoverorder.append("customerid", order.getCustomerID());
											leftoverorder.append("quantity", remainingshares);
											leftoverorder.append("actiontype", order.getActiontype());
											leftoverorder.append("cost", remainingshares * finalprice);
											leftoverorder.append("commission", finalcommission);
											leftoverorder.append("recordstatusdate", new Date());
											leftoverorder.append("recordstatus", 1);
											leftoverorder.append("price", finalprice);
											leftoverorder.append("extathleteid", externalathleteid);
											leftoverorder.append("athleteid", athleteid);
											leftoverorder.append("ispending", true);

											Double newvalue = (accountbalance - order.getCost()) * 100 * -1;
											int newbalance = newvalue.intValue();

											athlete.append("$push", new BasicDBObject().append("listorders", order));
											athlete.replace("availableshares", +availableshares + -updateshares);

											BasicDBObject transaction = new BasicDBObject();
											transaction.append("description", "Buy Order: " + athletename);
											transaction.append("amount", order.getCost());
											transaction.append("actiontype", "Buy");
											transaction.append("recordstatusdate", new Date());
											transaction.append("recordstatus", 1);

											if (isresellable) {
												int x = (order.getQuantity() / +totalshares);
												athlete.replace("currentprice", (+finalprice * +x) + +finalprice);

												BasicDBObject pricehistory = new BasicDBObject();
												pricehistory.append("price", athlete.get("currentprice"));
												pricehistory.append("isathletevalueprice", false);
												pricehistory.append("recordstatusdate", new Date());
												pricehistory.append("recordstatus", 1);
											}

											if (existingposition != null) {
												int currentquantity = existingposition.getInt("quantity");
												double currentcostpershare = existingposition.getDouble("costpershare");
												int newpositionquantity = +currentquantity + +updateshares;

												existingposition.replace("quantity", newpositionquantity);
												existingposition.replace("costpershare",
														((+currentcostpershare * +currentquantity)
																+ (+updateshares * +finalprice))
																/ +newpositionquantity);
												existingposition.replace("athletename", athletename);
												existingposition.replace("imageurl", athleteimageurl);
												existingposition.replace("recordstatusdate", new Date());
												existingposition.replace("recordstatus", 1);

												for (Object position : listofathletes) {
													BasicDBObject custposition = (BasicDBObject) position;
													if (custposition.get("athleteid") == athleteid) {
														existingposition = custposition;
														System.out.println("Found Athlete! Name:  " + existingposition);
													}
												}
											} else {
												BasicDBObject customerposition = new BasicDBObject();
												customerposition.append("quote", order.getQuote());
												customerposition.append("recordstatusdate", new Date());
												customerposition.append("recordstatus", 1);
												customerposition.append("costpershare", finalprice);
												customerposition.append("athleteid", athleteid);
												customerposition.append("athletename", athletename);
												customerposition.append("athletequote", athletequote);
												customerposition.append("imageurl", athleteimageurl);

												customer.append("$push",
														new BasicDBObject().append("listofathletes", customerposition));
											}

											if (paymentsapi.UpdateCustomerBalance(customer.getString("stripeaccount"),
													newbalance)) {

												if (avexDB.SaveAthlete(athlete)) {
													if (avexDB.SaveOrder(order)) {
														if (avexDB.SaveUser(customer)) {
															avexDB.CompleteAthleteQueue(athleteid);
															// TODO:SEND EMAIL
															// TO CUSTOMER
														} else {
															avexDB.CompleteAthleteQueue(athleteid);
															// LOG AND SEND
															// EMAIL TO CUSTOMER
															// WITH FAILURE
														}
													} else {
														avexDB.CompleteAthleteQueue(athleteid);
														// LOG AND SEND EMAIL TO
														// CUSTOMER WITH FAILURE
													}
												} else {
													avexDB.CompleteAthleteQueue(athleteid);
													// LOG AND SEND EMAIL TO
													// CUSTOMER WITH FAILURE
												}
											} else {
												avexDB.CompleteAthleteQueue(athleteid);
												// LOG AND SEND EMAIL TO
												// CUSTOMER WITH FAILURE
											}

										}
										// ORDER HAS BEEN COMPLETED
										else {
											Double newvalue = (accountbalance - order.getCost()) * 100 * -1;
											int newbalance = newvalue.intValue();

											athlete.append("$push", new BasicDBObject().append("listorders", order));
											athlete.replace("availableshares", +availableshares + -updateshares);

											BasicDBObject transaction = new BasicDBObject();
											transaction.append("description", "Buy Order: " + athletename);
											transaction.append("amount", order.getCost());
											transaction.append("actiontype", "Buy");
											transaction.append("recordstatusdate", new Date());
											transaction.append("recordstatus", 1);

											if (isresellable) {
												int x = (order.getQuantity() / +totalshares);
												athlete.replace("currentprice", (+finalprice * +x) + +finalprice);

												BasicDBObject pricehistory = new BasicDBObject();
												pricehistory.append("price", athlete.get("currentprice"));
												pricehistory.append("isathletevalueprice", false);
												pricehistory.append("recordstatusdate", new Date());
												pricehistory.append("recordstatus", 1);
											}

											if (existingposition != null) {
												int currentquantity = existingposition.getInt("quantity");
												double currentcostpershare = existingposition.getDouble("costpershare");
												int newpositionquantity = +currentquantity + +updateshares;

												existingposition.replace("quantity", newpositionquantity);
												existingposition.replace("costpershare",
														((+currentcostpershare * +currentquantity)
																+ (+updateshares * +finalprice))
																/ +newpositionquantity);
												existingposition.replace("athletename", athletename);
												existingposition.replace("imageurl", athleteimageurl);
												existingposition.replace("recordstatusdate", new Date());
												existingposition.replace("recordstatus", 1);

												for (Object position : listofathletes) {
													BasicDBObject custposition = (BasicDBObject) position;
													if (custposition.get("athleteid") == athleteid) {
														existingposition = custposition;
														System.out.println("Found Athlete! Name:  " + existingposition);
													}
												}
											} else {
												BasicDBObject customerposition = new BasicDBObject();
												customerposition.append("quote", order.getQuote());
												customerposition.append("recordstatusdate", new Date());
												customerposition.append("recordstatus", 1);
												customerposition.append("costpershare", finalprice);
												customerposition.append("athleteid", athleteid);
												customerposition.append("athletename", athletename);
												customerposition.append("athletequote", athletequote);
												customerposition.append("imageurl", athleteimageurl);

												customer.append("$push",
														new BasicDBObject().append("listofathletes", customerposition));
											}

											if (paymentsapi.UpdateCustomerBalance(customer.getString("stripeaccount"),
													newbalance)) {

												if (avexDB.SaveAthlete(athlete)) {
													if (avexDB.SaveOrder(order)) {
														if (avexDB.SaveUser(customer)) {
															avexDB.CompleteAthleteQueue(athleteid);
															// TODO:SEND EMAIL
															// TO CUSTOMER
														} else {
															avexDB.CompleteAthleteQueue(athleteid);
															// LOG AND SEND
															// EMAIL TO CUSTOMER
															// WITH FAILURE
														}
													} else {
														avexDB.CompleteAthleteQueue(athleteid);
														// LOG AND SEND EMAIL TO
														// CUSTOMER WITH FAILURE
													}
												} else {
													avexDB.CompleteAthleteQueue(athleteid);
													// LOG AND SEND EMAIL TO
													// CUSTOMER WITH FAILURE
												}
											} else {
												avexDB.CompleteAthleteQueue(athleteid);
												// LOG AND SEND EMAIL TO
												// CUSTOMER WITH FAILURE
											}
										}
									}
								}
								//INVALID BUY ORDER
								else {
									avexDB.CompleteAthleteQueue(athleteid);
									//LOG AND SEND EMAIL TO CUSTOMER WITH FAILURE
								}
							} else if (order.getActiontype().toLowerCase() == "sell") {
								if (!isresellable == true) {
									int x = (+order.getQuantity() / +totalshares);
									athlete.replace("currentprice", +finalprice - (+finalprice * +x));
									Double newvalue = (+accountbalance + +order.getCost()) * -1;
									int newaccountbalance = newvalue.intValue();

									if (paymentsapi.UpdateCustomerBalance(customer.getString("stripeaccount"),
											newaccountbalance)) {

										if (avexDB.SaveAthlete(athlete)) {
											if (avexDB.SaveOrder(order)) {
												if (avexDB.SaveUser(customer)) {
													avexDB.CompleteAthleteQueue(athleteid);
													// TODO:SEND EMAIL TO CUSTOMER
												}
												else{
													avexDB.CompleteAthleteQueue(athleteid);
													//LOG AND SEND EMAIL TO CUSTOMER WITH FAILURE
												}
											}
											else{
												avexDB.CompleteAthleteQueue(athleteid);
												//LOG AND SEND EMAIL TO CUSTOMER WITH FAILURE
											}
										}
										else{
											avexDB.CompleteAthleteQueue(athleteid);
											//LOG AND SEND EMAIL TO CUSTOMER WITH FAILURE
										}
									} else {
										avexDB.CompleteAthleteQueue(athleteid);
										//LOG AND SEND EMAIL TO CUSTOMER WITH FAILURE
									}
								}
								// THE ORDERS ARE RESELLABLE
								else {
									List<BasicDBObject> orders = avexDB.GetOrdersByAthleteId(2, athleteid);

									if (orders != null && !order.isEmpty()) {

									}

									updateshares = order.getQuantity();
									int initialQuantityRequest = updateshares;
									int remainingShares = 0;

									for (BasicDBObject nextorder : orders) {
										int nextorderquantity = nextorder.getInt("quantity");
										int newquantity = +nextorderquantity + +updateshares;
										int tempQuantity = updateshares;

										if (updateshares <= 0) {
											break;
										}

										if (nextorder.getString("customerid") != null) {
											BasicDBObject nextuser = avexDB.GetUser(nextorder.getString("customerid"));
											if (nextuser != null) {
												Customer nextcustomer = paymentsapi
														.GetCustomerBalance(nextuser.getString("stripeaccount"));
												BasicDBList nextuserlistofathletes = (BasicDBList) nextuser
														.get("listofathletes");
												BasicDBObject nextposition = new BasicDBObject();

												if (newquantity < 0) {
													nextorder.replace("quantity", 0);
													updateshares = newquantity;
													nextorder.replace("recordstatus", 3);
												} else {
													nextorder.replace("quantity", newquantity);
													updateshares = 0;
												}

												int nextuseraccountbalance = (+nextcustomer.getAccountBalance() * -1)
														/ 100;

												for (Object position : nextuserlistofathletes) {
													BasicDBObject custposition = (BasicDBObject) position;
													if (custposition.get("athleteid") == athleteid) {
														nextposition = custposition;
														System.out.println("Found Athlete! Name:  " + existingposition);
													}
												}

												if (nextposition != null && nextposition.getInt("recordstatus") != 3) {
													int nextcurrentquantity = nextposition.getInt("quantity");
													double nextcurrentcostpershare = nextposition.getDouble("costpershare");
													int nextrecordstatus = 2;

													double ordercost = tempQuantity * finalprice;
													newquantity = +nextcurrentquantity + +tempQuantity;

													if (newquantity == 0) {
														nextrecordstatus = 3;
													} else if (newquantity < 0) {
														continue;
													}

													nextposition.replace("quantity", newquantity);
													nextposition.replace("costpershare",
															((+nextcurrentquantity * +nextcurrentcostpershare)
																	+ (+ordercost)) / +newquantity);
													nextposition.replace("athletename", athletename);
													nextposition.replace("imageurl", athleteimageurl);
													nextposition.replace("recordstatusdate", new Date());
													nextposition.replace("recordstatus", nextrecordstatus);

													Double nextuservalue = ((+nextuseraccountbalance - +ordercost)
															* 100) * -1;
													int nextusernewbalance = nextuservalue.intValue();

													if (paymentsapi.UpdateCustomerBalance(
															nextuser.getString("stripeaccount"), nextusernewbalance)) {

														for (Object position : nextuserlistofathletes) {
															BasicDBObject custposition = (BasicDBObject) position;
															if (custposition.get("athleteid") == athleteid) {
																custposition = nextposition;
																System.out.println(
																		"Update Customer Position For Athlete! Name:  "
																				+ existingposition);
															}
														}

														nextuser.replace("listofathletes", nextuserlistofathletes);
														avexDB.SaveUser(nextuser);
													}
												}
											}
										} else {
											if (newquantity < 0) {
												nextorder.replace("quantity", 0);
												updateshares = newquantity;
												nextorder.replace("recordstatus", 3);
												nextorder.replace("isresellable", true);
											} else {
												nextorder.replace("quantity", newquantity);
												updateshares = 0;
											}
										}
									}

									// Process Remaining Order
									if (updateshares >= 0) {
										remainingshares = updateshares;
										updateshares = +initialQuantityRequest - +updateshares;
										order.setCost(+updateshares * +finalprice);
										order.setQuantity(updateshares);

										BasicDBObject leftoverorder = new BasicDBObject().append("quote", athletequote);
										leftoverorder.append("customerid", order.getCustomerID());
										leftoverorder.append("quantity", remainingshares);
										leftoverorder.append("actiontype", order.getActiontype());
										leftoverorder.append("cost", remainingshares * finalprice);
										leftoverorder.append("commission", finalcommission);
										leftoverorder.append("recordstatusdate", new Date());
										leftoverorder.append("recordstatus", 1);
										leftoverorder.append("price", finalprice);
										leftoverorder.append("extathleteid", externalathleteid);
										leftoverorder.append("athleteid", athleteid);
										leftoverorder.append("ispending", true);

										Double newvalue = (accountbalance - order.getCost()) * 100 * -1;
										int newbalance = newvalue.intValue();

										athlete.append("$push", new BasicDBObject().append("listorders", order));
										athlete.replace("availableshares", +availableshares + -updateshares);

										BasicDBObject transaction = new BasicDBObject();
										transaction.append("description", "Buy Order: " + athletename);
										transaction.append("amount", order.getCost());
										transaction.append("actiontype", "Buy");
										transaction.append("recordstatusdate", new Date());
										transaction.append("recordstatus", 1);

										if (isresellable) {
											int x = (order.getQuantity() / +totalshares);
											athlete.replace("currentprice", (+finalprice * +x) + +finalprice);

											BasicDBObject pricehistory = new BasicDBObject();
											pricehistory.append("price", athlete.get("currentprice"));
											pricehistory.append("isathletevalueprice", false);
											pricehistory.append("recordstatusdate", new Date());
											pricehistory.append("recordstatus", 1);
										}

										if (existingposition != null) {
											int currentquantity = existingposition.getInt("quantity");
											double currentcostpershare = existingposition.getDouble("costpershare");
											int newpositionquantity = +currentquantity + +updateshares;

											existingposition.replace("quantity", newpositionquantity);
											existingposition
													.replace("costpershare",
															((+currentcostpershare * +currentquantity)
																	+ (+updateshares * +finalprice))
																	/ +newpositionquantity);
											existingposition.replace("athletename", athletename);
											existingposition.replace("imageurl", athleteimageurl);
											existingposition.replace("recordstatusdate", new Date());
											existingposition.replace("recordstatus", 1);

											for (Object position : listofathletes) {
												BasicDBObject custposition = (BasicDBObject) position;
												if (custposition.get("athleteid") == athleteid) {
													existingposition = custposition;
													System.out.println("Found Athlete! Name:  " + existingposition);
												}
											}
										} else {
											BasicDBObject customerposition = new BasicDBObject();
											customerposition.append("quote", order.getQuote());
											customerposition.append("recordstatusdate", new Date());
											customerposition.append("recordstatus", 1);
											customerposition.append("costpershare", finalprice);
											customerposition.append("athleteid", athleteid);
											customerposition.append("athletename", athletename);
											customerposition.append("athletequote", athletequote);
											customerposition.append("imageurl", athleteimageurl);

											customer.append("$push",
													new BasicDBObject().append("listofathletes", customerposition));
										}

										if (paymentsapi.UpdateCustomerBalance(customer.getString("stripeaccount"),
												newbalance)) {

											if (avexDB.SaveAthlete(athlete)) {
												if (avexDB.SaveOrder(order)) {
													if (avexDB.SaveUser(customer)) {
														avexDB.CompleteAthleteQueue(athleteid);
														// TODO:SEND EMAIL TO CUSTOMER
													}
													else{
														avexDB.CompleteAthleteQueue(athleteid);
														//LOG AND SEND EMAIL TO CUSTOMER WITH FAILURE
													}
												}
												else{
													avexDB.CompleteAthleteQueue(athleteid);
													//LOG AND SEND EMAIL TO CUSTOMER WITH FAILURE
												}
											}
											else{
												avexDB.CompleteAthleteQueue(athleteid);
												//LOG AND SEND EMAIL TO CUSTOMER WITH FAILURE
											}
										} else {
											avexDB.CompleteAthleteQueue(athleteid);
											//LOG AND SEND EMAIL TO CUSTOMER WITH FAILURE
										}
									}
									// ORDER HAS BEEN COMPLETED
									else {
										Double newvalue = (accountbalance - order.getCost()) * 100 * -1;
										int newbalance = newvalue.intValue();

										athlete.append("$push", new BasicDBObject().append("listorders", order));
										athlete.replace("availableshares", +availableshares + -updateshares);

										BasicDBObject transaction = new BasicDBObject();
										transaction.append("description", "Buy Order: " + athletename);
										transaction.append("amount", order.getCost());
										transaction.append("actiontype", "Buy");
										transaction.append("recordstatusdate", new Date());
										transaction.append("recordstatus", 1);

										if (isresellable) {
											int x = (order.getQuantity() / +totalshares);
											athlete.replace("currentprice", (+finalprice * +x) + +finalprice);

											BasicDBObject pricehistory = new BasicDBObject();
											pricehistory.append("price", athlete.get("currentprice"));
											pricehistory.append("isathletevalueprice", false);
											pricehistory.append("recordstatusdate", new Date());
											pricehistory.append("recordstatus", 1);
										}

										if (existingposition != null) {
											int currentquantity = existingposition.getInt("quantity");
											double currentcostpershare = existingposition.getDouble("costpershare");
											int newpositionquantity = +currentquantity + +updateshares;

											existingposition.replace("quantity", newpositionquantity);
											existingposition
													.replace("costpershare",
															((+currentcostpershare * +currentquantity)
																	+ (+updateshares * +finalprice))
																	/ +newpositionquantity);
											existingposition.replace("athletename", athletename);
											existingposition.replace("imageurl", athleteimageurl);
											existingposition.replace("recordstatusdate", new Date());
											existingposition.replace("recordstatus", 1);

											for (Object position : listofathletes) {
												BasicDBObject custposition = (BasicDBObject) position;
												if (custposition.get("athleteid") == athleteid) {
													existingposition = custposition;
													System.out.println("Found Athlete! Name:  " + existingposition);
												}
											}
										} else {
											BasicDBObject customerposition = new BasicDBObject();
											customerposition.append("quote", order.getQuote());
											customerposition.append("recordstatusdate", new Date());
											customerposition.append("recordstatus", 1);
											customerposition.append("costpershare", finalprice);
											customerposition.append("athleteid", athleteid);
											customerposition.append("athletename", athletename);
											customerposition.append("athletequote", athletequote);
											customerposition.append("imageurl", athleteimageurl);

											customer.append("$push",
													new BasicDBObject().append("listofathletes", customerposition));
										}

										if (paymentsapi.UpdateCustomerBalance(customer.getString("stripeaccount"),
												newbalance)) {

											if (avexDB.SaveAthlete(athlete)) {
												if (avexDB.SaveOrder(order)) {
													if (avexDB.SaveUser(customer)) {
														avexDB.CompleteAthleteQueue(athleteid);
														// TODO:SEND EMAIL TO CUSTOMER
													}
													else{
														avexDB.CompleteAthleteQueue(athleteid);
														//LOG AND SEND EMAIL TO CUSTOMER WITH FAILURE
													}
												}
												else{
													avexDB.CompleteAthleteQueue(athleteid);
													//LOG AND SEND EMAIL TO CUSTOMER WITH FAILURE
												}
											}
											else{
												avexDB.CompleteAthleteQueue(athleteid);
												//LOG AND SEND EMAIL TO CUSTOMER WITH FAILURE
											}
										} else {
											avexDB.CompleteAthleteQueue(athleteid);
											//LOG AND SEND EMAIL TO CUSTOMER WITH FAILURE
										}
									}
								}
							}
						} else {
							System.out.println("Stripe Customer was not found");
						}
					} catch (AuthenticationException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (InvalidRequestException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (APIConnectionException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (CardException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (APIException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				} else {
					System.out.println("User was not found");
				}
			}
		}
		}
		catch(Exception ex){
			System.out.println("Exception: " + ex.getMessage());
			System.out.println("Stack Trace: " + ex.getStackTrace());
		}
	}
}