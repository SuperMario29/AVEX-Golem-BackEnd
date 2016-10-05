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
	private MainController mainControl = new MainController();
	private String ProcessOrders = "Your AVEX ORDER";
	private String FailedToProcessOrderMessage = "Your Order Failed To Process. Please Contact AVEX For Any Questions";

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
						o.setOrderid(order.getString("_id"));
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
					String orderid = "";
					int externalathleteid = 0;
					boolean isvalidorder = false;
					boolean isresellable = false;
					String customerID = order.getCustomerID();
					int remainingshares = 0;
					int updateshares = 0;
					
					BasicDBObject newathleteprice = new BasicDBObject();
					BasicDBObject newathleteshares = new BasicDBObject();
					BasicDBObject newathleteorder = new BasicDBObject();
					BasicDBObject newathletepricehistory = new BasicDBObject();
					
					BasicDBObject newcustomertransaction = new BasicDBObject();
					BasicDBObject newcustomerposition = new BasicDBObject();

					BasicDBObject existingposition = null;
					BasicDBObject customer = avexDB.GetUser(customerID);
					orderid = order.getOrderid();
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
							athleteid = athlete.getString("_id");
							long userqueue = avexDB.GetUserQueuePosition(athleteid);

							System.out.println("Wait Turn");

							while (!avexDB.CurrentQueue(userqueue, athleteid)) {
								try {
									Thread.sleep(1000);
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}

							System.out.println("Ready To Process!!");


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
								updateshares = order.getQuantity();
								BasicDBList listofathletes = (BasicDBList) customer.get("listofathletes");							


								if(listofathletes != null && listofathletes.size() > 0){
									for (Object position : listofathletes) {
										BasicDBObject custposition = (BasicDBObject) position;
										if (custposition.get("athleteid") == athleteid) {
											existingposition = custposition;
											System.out.println("Found Athlete! Name:  " + existingposition);
										}
									}
								}

								if (order.getActiontype().toLowerCase().contains("buy")) {
									if (availableshares > order.getQuantity() && accountbalance != 0.00) {
										List<BasicDBObject> orders = avexDB.GetOrdersByAthleteId(1, athleteid,externalathleteid);

										if (orders != null && !orders.isEmpty()) {
											int initialQuantityRequest = updateshares;
											int remainingShares = 0;

											for (BasicDBObject nextorder : orders) {
												if (updateshares <= 0) {
													break;
												}
												if (nextorder != null) {
													String nextorderid = nextorder.getString("_id");
													int nextorderquantity = nextorder.getInt("quantity");
													int newquantity = nextorderquantity - updateshares;
													int tempQuantity = updateshares;
													String nextcustomerid = nextorder.getString("customerid");
													BasicDBObject nextorderupdate = new BasicDBObject();
													
													if (nextcustomerid != null) {
														if (newquantity < 0) {
															nextorder.replace("quantity", 0);
															updateshares = newquantity;
															nextorder.replace("recordstatus", 3);
															nextorderupdate.append("quantity",0);
															nextorderupdate.append("recordstatus", 3);
														} else {
															nextorder.replace("quantity", newquantity);
															nextorderupdate.append("quantity",newquantity);
															nextorderupdate.append("recordstatus", 2);
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

																		BasicDBObject updatePosition = new BasicDBObject();
																		
																		for (Object position : nextuserlistofathletes) {
																			BasicDBObject custposition = (BasicDBObject) position;
																			if (custposition.get("athleteid") == athleteid) {
																				custposition = nextposition;
																				System.out.println(
																						"Update Customer Position For Athlete! Name:  "
																								+ existingposition);
																			}
																		}
																		
																		BasicDBObject transaction = new BasicDBObject();
																		transaction.append("description", "Sell Order: " + athletename + "Purchased");
																		transaction.append("amount", ordercost);
																		transaction.append("actiontype", "Sell");
																		transaction.append("recordstatusdate", new Date());
																		transaction.append("recordstatus", 1);
																		BasicDBObject nexttransaction = new BasicDBObject();

																		nexttransaction.append("$push", new BasicDBObject().append("listoftransactions", transaction));

																		updatePosition.put("$push", nextuserlistofathletes);
																		if (avexDB.UpdateOrder(nextorderid,nextorderupdate)) {
																			if (avexDB.SaveUser(nextuser,updatePosition,nexttransaction)) {
																				
																			}
																		}
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
															nextorderupdate.append("quantity",0);
															nextorderupdate.append("recordstatus", 3);
															nextorderupdate.append("isresellable",true);
														} else {
															nextorder.replace("quantity", newquantity);
															nextorderupdate.append("quantity",newquantity);
															nextorderupdate.append("recordstatus", 2);
															updateshares = 0;
														}
														
														if (avexDB.UpdateOrder(nextorderid,nextorderupdate)) {

														}
													}
												}
											}

											// Process Remaining Order
											if (updateshares > 0) {
												BasicDBObject orderupdate = new BasicDBObject();
												remainingshares = updateshares;
												updateshares = +initialQuantityRequest - +updateshares;
												order.setCost(+updateshares * +finalprice);
												order.setQuantity(updateshares);
												order.setIspending(false);
												orderupdate.append("cost", order.getCost());
												orderupdate.append("ispending", false);
												orderupdate.append("recordstatus", 3);
												
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

												newathleteorder.append("$push", new BasicDBObject().append("listorders", order));
												newathleteshares.append("$set", new BasicDBObject().append("availableshares", +availableshares + -updateshares));

												BasicDBObject transaction = new BasicDBObject();
												transaction.append("description", "Buy Order: " + athletename);
												transaction.append("amount", order.getCost());
												transaction.append("actiontype", "Buy");
												transaction.append("recordstatusdate", new Date());
												transaction.append("recordstatus", 1);
												
												newcustomertransaction.append("$push", new BasicDBObject().append("listoftransactions", transaction));

												if (isresellable) {
													int x = (order.getQuantity() / +totalshares);
													newathleteprice.append("$set", new BasicDBObject().append("currentprice", (+finalprice * +x) + +finalprice));

													BasicDBObject pricehistory = new BasicDBObject();
													pricehistory.append("price", athlete.get("currentprice"));
													pricehistory.append("isathletevalueprice", false);
													pricehistory.append("recordstatusdate", new Date());
													pricehistory.append("recordstatus", 1);
													newathletepricehistory.append("$push", new BasicDBObject().append("pricehistory", pricehistory));
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
															 custposition = existingposition;
															System.out.println("Found Athlete! Name:  " + existingposition);
														}
													}
													
													newcustomerposition.append("$set", new BasicDBObject().append("listofathletes", listofathletes));
												} else {
													BasicDBObject customerposition = new BasicDBObject();
													customerposition.append("quote", order.getQuote());
													customerposition.append("quantity", order.getQuantity());
													customerposition.append("recordstatusdate", new Date());
													customerposition.append("recordstatus", 1);
													customerposition.append("costpershare", finalprice);
													customerposition.append("athleteid", athleteid);
													customerposition.append("athletename", athletename);
													customerposition.append("athletequote", athletequote);
													customerposition.append("imageurl", athleteimageurl);
													
													newcustomerposition.put("$push",
															new BasicDBObject().append("listofathletes", customerposition));
												}

												if (paymentsapi.UpdateCustomerBalance(customer.getString("stripeaccount"),
														newbalance)) {

													if (avexDB.SaveAthlete(athlete,newathleteprice,newathleteshares,newathleteorder,newathletepricehistory)) {
														if(avexDB.CreateNewOrder(leftoverorder)){
															if (avexDB.UpdateOrder(orderid,orderupdate)) {
																if (avexDB.SaveUser(customer,newcustomerposition,newcustomertransaction)) {
																	avexDB.CompleteAthleteQueue(athleteid);
																	String message = "Order For Athlete: " + athletename + " was completed successfully. There are " + remainingshares + " quantity to be purchanged.";
																	mainControl.SendEmail(customer.getString("emailaddress"), message, ProcessOrders);
																} else {
																	avexDB.CompleteAthleteQueue(athleteid);
																	// LOG FAILURE
																	mainControl.SendEmail(customer.getString("emailaddress"), FailedToProcessOrderMessage, ProcessOrders);

																}
															} else {
																avexDB.CompleteAthleteQueue(athleteid);
																System.out.println("Proessing Order Failed: CustomerID-" + customerID + "AthleteName: " + athleteid + "");
																mainControl.SendEmail(customer.getString("emailaddress"), FailedToProcessOrderMessage, ProcessOrders);
															}
														}
														else{
															avexDB.CompleteAthleteQueue(athleteid);
															System.out.println("Proessing Order Failed: CustomerID-" + customerID + "AthleteName: " + athleteid + "");
															mainControl.SendEmail(customer.getString("emailaddress"), FailedToProcessOrderMessage, ProcessOrders);
														}
													} else {
														avexDB.CompleteAthleteQueue(athleteid);
														System.out.println("Proessing Order Failed: CustomerID-" + customerID + "AthleteName: " + athleteid + "");
														mainControl.SendEmail(customer.getString("emailaddress"), FailedToProcessOrderMessage, ProcessOrders);
													}
												} else {
													avexDB.CompleteAthleteQueue(athleteid);
													System.out.println("Proessing Order Failed: CustomerID-" + customerID + "AthleteName: " + athleteid + "");
													mainControl.SendEmail(customer.getString("emailaddress"), FailedToProcessOrderMessage, ProcessOrders);
												}
											}
											// ORDER HAS BEEN COMPLETED
											else {
												BasicDBObject orderupdate = new BasicDBObject();
												order.setCost(order.getQuantity() * +finalprice);
												order.setIspending(false);
												Double newvalue = (accountbalance - order.getCost()) * 100 * -1;
												int newbalance = newvalue.intValue();
												
												orderupdate.append("cost", order.getCost());
												orderupdate.append("quantity", order.getQuantity());
												orderupdate.append("ispending", false);
												orderupdate.append("recordstatusdate", new Date());
												
												newathleteorder.append("$push", new BasicDBObject().append("listorders", order));
												newathleteshares.append("$set",new BasicDBObject().append("availableshares", +availableshares - order.getQuantity()));

												BasicDBObject transaction = new BasicDBObject();
												transaction.append("description", "Buy Order: " + athletename);
												transaction.append("amount", order.getCost());
												transaction.append("actiontype", "Buy");
												transaction.append("recordstatusdate", new Date());
												transaction.append("recordstatus", 1);
												
												newcustomertransaction.append("$push", new BasicDBObject().append("listoftransactions", transaction));

												if (isresellable) {
													int x = (order.getQuantity() / +totalshares);
													newathleteprice.append("$set", new BasicDBObject().append("currentprice", (+finalprice * +x) + +finalprice));

													BasicDBObject pricehistory = new BasicDBObject();
													pricehistory.append("price", athlete.get("currentprice"));
													pricehistory.append("isathletevalueprice", false);
													pricehistory.append("recordstatusdate", new Date());
													pricehistory.append("recordstatus", 1);
													
													newathletepricehistory.append("$push", new BasicDBObject().append("pricehistory", pricehistory));
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
															 custposition = existingposition;
															System.out.println("Found Athlete! Name:  " + existingposition);
														}
													}
													newcustomerposition.put("$set", new BasicDBObject().append("listofathletes", listofathletes));
													
												} else {
													BasicDBObject customerposition = new BasicDBObject();
													customerposition.append("quote", order.getQuote());
													customerposition.append("recordstatusdate", new Date());
													customerposition.append("quantity", order.getQuantity());
													customerposition.append("recordstatus", 1);
													customerposition.append("costpershare", finalprice);
													customerposition.append("athleteid", athleteid);
													customerposition.append("athletename", athletename);
													customerposition.append("athletequote", athletequote);
													customerposition.append("imageurl", athleteimageurl);

													newcustomerposition.put("$push",
															new BasicDBObject().append("listofathletes", customerposition));
												}

												if (paymentsapi.UpdateCustomerBalance(customer.getString("stripeaccount"),
														newbalance)) {
													if (avexDB.SaveAthlete(athlete,newathleteprice,newathleteshares,newathleteorder,newathletepricehistory)) {														
														if (avexDB.UpdateOrder(orderid,orderupdate)) {
															if (avexDB.SaveUser(customer,newcustomerposition,newcustomertransaction)) {
																avexDB.CompleteAthleteQueue(athleteid);
																String message = "Order For Athlete: " + athletename + " was completed successfully. There are " + remainingshares + " quantity to be purchanged.";
																mainControl.SendEmail(customer.getString("emailaddress"), message, ProcessOrders);
															} else {
																avexDB.CompleteAthleteQueue(athleteid);
																System.out.println("Proessing Order Failed: CustomerID-" + customerID + "AthleteName: " + athleteid + "");
																mainControl.SendEmail(customer.getString("emailaddress"), FailedToProcessOrderMessage, ProcessOrders);
															}
														} else {
															avexDB.CompleteAthleteQueue(athleteid);
															System.out.println("Proessing Order Failed: CustomerID-" + customerID + "AthleteName: " + athleteid + "");
															mainControl.SendEmail(customer.getString("emailaddress"), FailedToProcessOrderMessage, ProcessOrders);
														}
													} else {
														avexDB.CompleteAthleteQueue(athleteid);
														System.out.println("Proessing Order Failed: CustomerID-" + customerID + "AthleteName: " + athleteid + "");
														mainControl.SendEmail(customer.getString("emailaddress"), FailedToProcessOrderMessage, ProcessOrders);
													}
												} else {
													avexDB.CompleteAthleteQueue(athleteid);
													System.out.println("Proessing Order Failed: CustomerID-" + customerID + "AthleteName: " + athleteid + "");
													mainControl.SendEmail(customer.getString("emailaddress"), FailedToProcessOrderMessage, ProcessOrders);
												}
											}
										}
										else{
											avexDB.CompleteAthleteQueue(athleteid);
											System.out.println("Proessing Order Failed: CustomerID-" + customerID + "AthleteName: " + athleteid + "");
											mainControl.SendEmail(customer.getString("emailaddress"), FailedToProcessOrderMessage, ProcessOrders);
										}
									}
									//NO ORDERS TO BUY ORDER
									else {
										//INCREMENT ATHLETE PRICE HIGHER
										if (isresellable) {
											//int x = (order.getQuantity() / +totalshares);
											//newathleteprice.append("$set", new BasicDBObject().append("currentprice", (+finalprice * +x) + +finalprice));
											newathleteprice.append("$set", new BasicDBObject().append("currentprice", (+finalprice * + .001) + +finalprice));
											
											BasicDBObject pricehistory = new BasicDBObject();
											pricehistory.append("price", athlete.get("currentprice"));
											pricehistory.append("isathletevalueprice", false);
											pricehistory.append("recordstatusdate", new Date());
											pricehistory.append("recordstatus", 1);
											newathletepricehistory.append("$push", new BasicDBObject().append("pricehistory", pricehistory));
											
											avexDB.SaveAthlete(athlete, newathleteprice, null, null, newathletepricehistory);
										}									
										
										avexDB.CompleteAthleteQueue(athleteid);
										//LOG AND SEND EMAIL TO CUSTOMER WITH FAILURE
									}
								} else if (order.getActiontype().toLowerCase() == "sell" && existingposition != null) {
									if (!isresellable == true) {
										BasicDBObject orderupdate = new BasicDBObject();
										
										int x = (+order.getQuantity() / +totalshares);
										newathleteprice.append("$set", new BasicDBObject().append("currentprice", +finalprice - (+finalprice * +x)));
										newathleteshares.append("$set", new BasicDBObject().append("availableshares", +availableshares + order.getQuantity()));										
										
										Double newvalue = (+accountbalance + +order.getCost()) * -1;
										int newaccountbalance = newvalue.intValue();
										
										int currentquantity = existingposition.getInt("quantity");
										double currentcostpershare = existingposition.getDouble("costpershare");
										int newpositionquantity = +currentquantity - +updateshares;
										
										orderupdate.append("recordstatus", 3);
										orderupdate.append("cost", order.getCost());

										existingposition.replace("quantity", newpositionquantity);
										existingposition.replace("costpershare",
												((+currentquantity
														* +currentcostpershare)
														- (+order.getCost()) / +updateshares));
										existingposition.replace("athletename",
												athlete.get("name"));
										existingposition.replace("imageurl",
												athlete.getString("imageurl"));
										existingposition.replace("recordstatusdate", new Date());
										existingposition.replace("recordstatus", 3);

										for (Object position : listofathletes) {
											BasicDBObject custposition = (BasicDBObject) position;
											if (custposition.get("athleteid") == athleteid) {
												 custposition = existingposition;
												System.out.println("Found Athlete! Name:  " + existingposition);
											}
										}
										newcustomerposition.put("$set", new BasicDBObject().append("listofathletes", listofathletes));
										
										BasicDBObject transaction = new BasicDBObject();
										transaction.append("description", "Sell Order: " + athletename + "Purchased");
										transaction.append("amount", order.getCost());
										transaction.append("actiontype", "Sell");
										transaction.append("recordstatusdate", new Date());
										transaction.append("recordstatus", 1);
										
										newcustomertransaction.append("$push", new BasicDBObject().append("listoftransactions", transaction));


										if (paymentsapi.UpdateCustomerBalance(customer.getString("stripeaccount"),
												newaccountbalance)) {

											if (avexDB.SaveAthlete(athlete,newathleteprice,newathleteshares,newathleteorder,newathletepricehistory)) {
												if (avexDB.UpdateOrder(orderid,orderupdate)) {
													if (avexDB.SaveUser(customer,newcustomerposition,newcustomertransaction)) {
														avexDB.CompleteAthleteQueue(athleteid);
														String message = "Order For Athlete: " + athletename + " was completed successfully. ";
														mainControl.SendEmail(customer.getString("emailaddress"), message, ProcessOrders);
													}
													else{
														avexDB.CompleteAthleteQueue(athleteid);
														System.out.println("Proessing Order Failed: CustomerID-" + customerID + "AthleteName: " + athleteid + "");
														mainControl.SendEmail(customer.getString("emailaddress"), FailedToProcessOrderMessage, ProcessOrders);
													}
												}
												else{
													avexDB.CompleteAthleteQueue(athleteid);
													System.out.println("Proessing Order Failed: CustomerID-" + customerID + "AthleteName: " + athleteid + "");
													mainControl.SendEmail(customer.getString("emailaddress"), FailedToProcessOrderMessage, ProcessOrders);
												}
											}
											else{
												avexDB.CompleteAthleteQueue(athleteid);
												System.out.println("Proessing Order Failed: CustomerID-" + customerID + "AthleteName: " + athleteid + "");
												mainControl.SendEmail(customer.getString("emailaddress"), FailedToProcessOrderMessage, ProcessOrders);
											}
										} else {
											avexDB.CompleteAthleteQueue(athleteid);
											System.out.println("Proessing Order Failed: CustomerID-" + customerID + "AthleteName: " + athleteid + "");
											mainControl.SendEmail(customer.getString("emailaddress"), FailedToProcessOrderMessage, ProcessOrders);
										}
									}
									// THE ORDERS ARE RESELLABLE
									else {
										List<BasicDBObject> orders = avexDB.GetOrdersByAthleteId(2, athleteid,externalathleteid);

										if (orders != null && !order.isEmpty()) {

											updateshares = order.getQuantity();
											int initialQuantityRequest = updateshares;
											int remainingShares = 0;

											for (BasicDBObject nextorder : orders) {
												int nextorderquantity = nextorder.getInt("quantity");
												BasicDBObject nextorderupdate = new BasicDBObject();
												int newquantity = +nextorderquantity + +updateshares;
												int tempQuantity = updateshares;
												String nextorderid = nextorder.getString("_id");

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
															nextorderupdate.append("quantity", 0);
															nextorderupdate.append("recordstatus", 3);
															
														} else {
															nextorder.replace("quantity", newquantity);
															nextorderupdate.append("quantity", newquantity);
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

																BasicDBObject updatePosition = new BasicDBObject();
																
																for (Object position : nextuserlistofathletes) {
																	BasicDBObject custposition = (BasicDBObject) position;
																	if (custposition.get("athleteid") == athleteid) {
																		custposition = nextposition;
																		System.out.println(
																				"Update Customer Position For Athlete! Name:  "
																						+ existingposition);
																	}
																}
																updatePosition.put("$push", nextuserlistofathletes);
																
																BasicDBObject transaction = new BasicDBObject();
																transaction.append("description", "Buy Order: " + athletename + "Completed");
																transaction.append("amount", ordercost);
																transaction.append("actiontype", "Buy");
																transaction.append("recordstatusdate", new Date());
																transaction.append("recordstatus", 1);
																BasicDBObject nexttransaction = new BasicDBObject();

																nexttransaction.append("$push", new BasicDBObject().append("listoftransactions", transaction));
																
																if (avexDB.UpdateOrder(nextorderid,nextorderupdate)) {
																	avexDB.SaveUser(nextuser,updatePosition,nexttransaction);
																}
															}
														}
													}
												} else {
													if (newquantity < 0) {
														nextorder.replace("quantity", 0);
														updateshares = newquantity;
														nextorder.replace("recordstatus", 3);
														nextorder.replace("isresellable", true);
														nextorderupdate.append("quantity",0);
														nextorderupdate.append("recordstatus", 3);
														nextorderupdate.append("isresellable",true);	
													} else {
														nextorder.replace("quantity", newquantity);
														nextorderupdate.append("quantity",newquantity);
														nextorderupdate.append("recordstatus", 2);
														updateshares = 0;
													}
												}
												if (avexDB.UpdateOrder(nextorderid,nextorderupdate)) {

												}
											}

											// Process Remaining Order
											if (updateshares > 0) {
												BasicDBObject orderupdate = new BasicDBObject();
												remainingshares = updateshares;
												updateshares = +initialQuantityRequest - +updateshares;
												order.setCost(+updateshares * +finalprice);
												order.setQuantity(updateshares);
												orderupdate.append("cost", order.getCost());
												orderupdate.append("ispending", false);
												orderupdate.append("recordstatus", 3);

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

												newathleteorder.append("$push", new BasicDBObject().append("listorders", order));
												newathleteshares.append("$set", new BasicDBObject().append("availableshares", +availableshares + -updateshares));

												BasicDBObject transaction = new BasicDBObject();
												transaction.append("description", "Buy Order: " + athletename);
												transaction.append("amount", order.getCost());
												transaction.append("actiontype", "Buy");
												transaction.append("recordstatusdate", new Date());
												transaction.append("recordstatus", 1);
												
												newcustomertransaction.append("$push", new BasicDBObject().append("listoftransactions", transaction));

												if (isresellable) {
													int x = (order.getQuantity() / +totalshares);
													newathleteprice.append("$set", new BasicDBObject().append("currentprice", (+finalprice * +x) + +finalprice));

													BasicDBObject pricehistory = new BasicDBObject();
													pricehistory.append("price", athlete.get("currentprice"));
													pricehistory.append("isathletevalueprice", false);
													pricehistory.append("recordstatusdate", new Date());
													pricehistory.append("recordstatus", 1);
													
													newathletepricehistory.append("$push", new BasicDBObject().append("pricehistory", pricehistory));
												}
												
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
															 custposition=existingposition;
															System.out.println("Found Athlete! Name:  " + existingposition);
														}
													}
													
													newcustomerposition.put("$push", new BasicDBObject().append("listofathletes", listofathletes));
																																							
												if (paymentsapi.UpdateCustomerBalance(customer.getString("stripeaccount"),
														newbalance)) {

													if (avexDB.SaveAthlete(athlete,newathleteprice,newathleteshares,newathleteorder,newathletepricehistory)) {
														if(avexDB.CreateNewOrder(leftoverorder)){
															if (avexDB.UpdateOrder(orderid,orderupdate)) {
																if (avexDB.SaveUser(customer,newcustomerposition,newcustomertransaction)) {
																	avexDB.CompleteAthleteQueue(athleteid);
																	String message = "Order For Athlete: " + athletename + " was completed successfully. There are " + remainingshares + " quantity to be purchanged.";
																	mainControl.SendEmail(customer.getString("emailaddress"), message, ProcessOrders);
																}
																else{
																	avexDB.CompleteAthleteQueue(athleteid);
																	System.out.println("Proessing Order Failed: CustomerID-" + customerID + "AthleteName: " + athleteid + "");
																	mainControl.SendEmail(customer.getString("emailaddress"), FailedToProcessOrderMessage, ProcessOrders);
																}
															}
															else{
																avexDB.CompleteAthleteQueue(athleteid);
																System.out.println("Proessing Order Failed: CustomerID-" + customerID + "AthleteName: " + athleteid + "");
																mainControl.SendEmail(customer.getString("emailaddress"), FailedToProcessOrderMessage, ProcessOrders);
															}
														}
														else{
															avexDB.CompleteAthleteQueue(athleteid);
															System.out.println("Proessing Order Failed: CustomerID-" + customerID + "AthleteName: " + athleteid + "");
															mainControl.SendEmail(customer.getString("emailaddress"), FailedToProcessOrderMessage, ProcessOrders);
														}
													}
													else{
														avexDB.CompleteAthleteQueue(athleteid);
														System.out.println("Proessing Order Failed: CustomerID-" + customerID + "AthleteName: " + athleteid + "");
														mainControl.SendEmail(customer.getString("emailaddress"), FailedToProcessOrderMessage, ProcessOrders);
													}
												} else {
													avexDB.CompleteAthleteQueue(athleteid);
													System.out.println("Proessing Order Failed: CustomerID-" + customerID + "AthleteName: " + athleteid + "");
													mainControl.SendEmail(customer.getString("emailaddress"), FailedToProcessOrderMessage, ProcessOrders);
												}
											}
											// ORDER HAS BEEN COMPLETED
											else {
												BasicDBObject orderupdate = new BasicDBObject();
												order.setCost(+order.getQuantity() * +finalprice);
												Double newvalue = (accountbalance - order.getCost()) * 100 * -1;
												int newbalance = newvalue.intValue();
												
												orderupdate.append("cost", order.getCost());
												orderupdate.append("quantity", order.getQuantity());
												orderupdate.append("ispending", false);

												newathleteorder.append("$push", new BasicDBObject().append("listorders", order));
												newathleteshares.append("$set", new BasicDBObject().append("availableshares", +availableshares - order.getQuantity()));

												BasicDBObject transaction = new BasicDBObject();
												transaction.append("description", "Sell Order: " + athletename + "Completed");
												transaction.append("amount", order.getCost());
												transaction.append("actiontype", "Sell");
												transaction.append("recordstatusdate", new Date());
												transaction.append("recordstatus", 1);
												
												newcustomertransaction.append("$push", new BasicDBObject().append("listoftransactions", transaction));

												if (isresellable) {
													int x = (order.getQuantity() / +totalshares);
													newathleteprice.append("$set", new BasicDBObject().append("currentprice", (+finalprice * +x) + +finalprice));

													BasicDBObject pricehistory = new BasicDBObject();
													pricehistory.append("price", athlete.get("currentprice"));
													pricehistory.append("isathletevalueprice", false);
													pricehistory.append("recordstatusdate", new Date());
													pricehistory.append("recordstatus", 1);
													
													newathletepricehistory.append("$push", new BasicDBObject().append("pricehistory", pricehistory));
												}
												
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
															 custposition=existingposition;
															System.out.println("Found Athlete! Name:  " + existingposition);
														}
													}
													
													newcustomerposition.put("$set", new BasicDBObject().append("listofathletes", listofathletes));

												if (paymentsapi.UpdateCustomerBalance(customer.getString("stripeaccount"),
														newbalance)) {

													if (avexDB.SaveAthlete(athlete,newathleteprice,newathleteshares,newathleteorder,newathletepricehistory)) {
														if (avexDB.UpdateOrder(orderid,orderupdate)) {
															if (avexDB.SaveUser(customer,newcustomerposition,newcustomertransaction)) {
																avexDB.CompleteAthleteQueue(athleteid);
																String message = "Order For Athlete: " + athletename + " was completed successfully. There are " + remainingshares + " quantity to be purchanged.";
																mainControl.SendEmail(customer.getString("emailaddress"), message, ProcessOrders);
															}
															else{
																avexDB.CompleteAthleteQueue(athleteid);
																System.out.println("Proessing Order Failed: CustomerID-" + customerID + "AthleteName: " + athleteid + "");
																mainControl.SendEmail(customer.getString("emailaddress"), FailedToProcessOrderMessage, ProcessOrders);
															}
														}
														else{
															avexDB.CompleteAthleteQueue(athleteid);
															System.out.println("Proessing Order Failed: CustomerID-" + customerID + "AthleteName: " + athleteid + "");
															mainControl.SendEmail(customer.getString("emailaddress"), FailedToProcessOrderMessage, ProcessOrders);
														}
													}
													else{
														avexDB.CompleteAthleteQueue(athleteid);
														System.out.println("Proessing Order Failed: CustomerID-" + customerID + "AthleteName: " + athleteid + "");
														mainControl.SendEmail(customer.getString("emailaddress"), FailedToProcessOrderMessage, ProcessOrders);
													}
												} else {
													avexDB.CompleteAthleteQueue(athleteid);
													System.out.println("Proessing Order Failed: CustomerID-" + customerID + "AthleteName: " + athleteid + "");
													mainControl.SendEmail(customer.getString("emailaddress"), FailedToProcessOrderMessage, ProcessOrders);
												}
											}
										}
										else{									
											avexDB.CompleteAthleteQueue(athleteid);
											System.out.println("Proessing Order Failed: CustomerID-" + customerID + "AthleteName: " + athleteid + "");
											mainControl.SendEmail(customer.getString("emailaddress"), FailedToProcessOrderMessage, ProcessOrders);
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