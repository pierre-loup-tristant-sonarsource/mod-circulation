package org.folio.circulation.domain;

import java.util.Optional;
import java.util.UUID;

import org.folio.circulation.domain.representations.CheckInByBarcodeRequest;
import org.folio.circulation.support.ClockManager;
import org.joda.time.DateTime;

/**
 * The loan captures a snapshot of the item status
 * in order to populate the loan action history.
 *
 * This means that the check in process needs to remember
 * when a loan is closed, until after the item status is
 * updated.
 *
 * Which requires passing the records between processes.
 */
public class CheckInContext {
  private final CheckInByBarcodeRequest checkInRequest;
  private final Item item;
  private final Loan loan;
  private final RequestQueue requestQueue;
  private final ServicePoint checkInServicePoint;
  private final Request highestPriorityFulfillableRequest;
  private final String loggedInUserId;
  private final DateTime checkInProcessedDateTime;
  private final boolean inHouseUse;
  private final ItemStatus itemStatusBeforeCheckIn;
  private final boolean lostItemFeesRefundedOrCancelled;

  public CheckInContext(CheckInByBarcodeRequest checkInRequest) {
    this(checkInRequest, null, null, null, null, null, null, false, null, false);
  }

  private CheckInContext(
    CheckInByBarcodeRequest checkInRequest,
    Item item,
    Loan loan,
    RequestQueue requestQueue,
    ServicePoint checkInServicePoint,
    Request highestPriorityFulfillableRequest,
    String loggedInUserId,
    boolean inHouseUse,
    ItemStatus itemStatusBeforeCheckIn,
    boolean lostItemFeesRefundedOrCancelled) {

    this.checkInRequest = checkInRequest;
    this.item = item;
    this.loan = loan;
    this.requestQueue = requestQueue;
    this.checkInServicePoint = checkInServicePoint;
    this.highestPriorityFulfillableRequest = highestPriorityFulfillableRequest;
    this.loggedInUserId = loggedInUserId;
    checkInProcessedDateTime = ClockManager.getClockManager().getDateTime();
    this.inHouseUse = inHouseUse;
    this.itemStatusBeforeCheckIn = itemStatusBeforeCheckIn;
    this.lostItemFeesRefundedOrCancelled = lostItemFeesRefundedOrCancelled;
  }

  public CheckInContext withItem(Item item) {

    //When the item is updated, also update the item for the loan,
    //as they should be the same
    final Loan updatedLoan = Optional.ofNullable(loan)
      .map(l -> l.withItem(item))
      .orElse(null);

    return new CheckInContext(
      this.checkInRequest,
      item,
      updatedLoan,
      this.requestQueue,
      this.checkInServicePoint,
      this.highestPriorityFulfillableRequest,
      this.loggedInUserId,
      this.inHouseUse,
      itemStatusBeforeCheckIn,
      lostItemFeesRefundedOrCancelled);
  }

  public CheckInContext withLoan(Loan loan) {
    return new CheckInContext(
      this.checkInRequest,
      this.item,
      loan,
      this.requestQueue,
      this.checkInServicePoint,
      this.highestPriorityFulfillableRequest,
      loggedInUserId,
      this.inHouseUse,
      itemStatusBeforeCheckIn,
      lostItemFeesRefundedOrCancelled);
  }

  public CheckInContext withRequestQueue(RequestQueue requestQueue) {
    Request firstRequest = null;
    if (requestQueue.hasOutstandingFulfillableRequests()) {
      firstRequest = requestQueue.getHighestPriorityFulfillableRequest();
    }
    return new CheckInContext(
      this.checkInRequest,
      this.item,
      this.loan,
      requestQueue,
      this.checkInServicePoint,
      firstRequest,
      loggedInUserId,
      this.inHouseUse,
      itemStatusBeforeCheckIn,
      lostItemFeesRefundedOrCancelled);
  }

  public CheckInContext withCheckInServicePoint(ServicePoint checkInServicePoint) {
    return new CheckInContext(
      this.checkInRequest,
      this.item,
      this.loan,
      this.requestQueue,
      checkInServicePoint,
      this.highestPriorityFulfillableRequest,
      loggedInUserId,
      this.inHouseUse,
      itemStatusBeforeCheckIn,
      lostItemFeesRefundedOrCancelled);
  }

  public CheckInContext withHighestPriorityFulfillableRequest(Request request) {
    return new CheckInContext(
      this.checkInRequest,
      this.item,
      this.loan,
      this.requestQueue,
      this.checkInServicePoint,
      request,
      loggedInUserId,
      this.inHouseUse,
      itemStatusBeforeCheckIn,
      lostItemFeesRefundedOrCancelled);
  }

  public CheckInContext withLoggedInUserId(String userId) {
    return new CheckInContext(
      this.checkInRequest,
      this.item,
      this.loan,
      this.requestQueue,
      this.checkInServicePoint,
      this.highestPriorityFulfillableRequest,
      userId,
      this.inHouseUse,
      itemStatusBeforeCheckIn,
      lostItemFeesRefundedOrCancelled);
  }

  public CheckInContext withInHouseUse(boolean inHouseUse) {
    return new CheckInContext(
      this.checkInRequest,
      this.item,
      this.loan,
      this.requestQueue,
      this.checkInServicePoint,
      this.highestPriorityFulfillableRequest,
      this.loggedInUserId,
      inHouseUse,
      itemStatusBeforeCheckIn,
      lostItemFeesRefundedOrCancelled);
  }

  public CheckInContext withItemStatusBeforeCheckIn(ItemStatus itemStatus) {
    return new CheckInContext(
      this.checkInRequest,
      this.item,
      this.loan,
      this.requestQueue,
      this.checkInServicePoint,
      this.highestPriorityFulfillableRequest,
      this.loggedInUserId,
      inHouseUse,
      itemStatus,
      lostItemFeesRefundedOrCancelled);
  }

  public CheckInContext withLostItemFeesRefundedOrCancelled(boolean lostItemFeesRefunded) {
    return new CheckInContext(
      this.checkInRequest,
      this.item,
      this.loan,
      this.requestQueue,
      this.checkInServicePoint,
      this.highestPriorityFulfillableRequest,
      this.loggedInUserId,
      inHouseUse,
      itemStatusBeforeCheckIn,
      lostItemFeesRefunded);
  }

  public boolean isInHouseUse() {
    return inHouseUse;
  }

  public String getCheckInRequestBarcode() {
    return checkInRequest.getItemBarcode();
  }

  public UUID getCheckInServicePointId() {
    return checkInRequest.getServicePointId();
  }

  public Item getItem() {
    return item;
  }

  public Loan getLoan() {
    return loan;
  }

  public CheckInByBarcodeRequest getCheckInRequest() {
    return checkInRequest;
  }

  public RequestQueue getRequestQueue() {
    return requestQueue;
  }

  public ServicePoint getCheckInServicePoint() {
    return checkInServicePoint;
  }

  public Request getHighestPriorityFulfillableRequest() {
    return highestPriorityFulfillableRequest;
  }

  public String getLoggedInUserId() {
    return loggedInUserId;
  }

  public DateTime getCheckInProcessedDateTime() {
    return checkInProcessedDateTime;
  }

  public ItemStatus getItemStatusBeforeCheckIn() {
    return itemStatusBeforeCheckIn;
  }

  public boolean areLostItemFeesRefundedOrCancelled() {
    return lostItemFeesRefundedOrCancelled;
  }
}
