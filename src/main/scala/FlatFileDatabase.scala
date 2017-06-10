import java.util.Date

import scala.io.Source

/**
  * Created by robert-blankenship on 6/9/17.
  */
class FlatFileDatabase(customersFilePath: String, employeesFilePath: String, ordersFilePath: String,
                       orderDetailsFilePath: String, productsFilePath: String) {

  case class Customer(id: String, companyName: String, contact: Contact, city: String, state: String)
  case class Contact(firstName: String, lastName: String, jobTitle: String)

  case class Employee(id: String, firstName: String, lastName: String, title: String)

  case class Order(id: String, customerId: String, employeeId: String, orderDate: Date, shippedDate: Date, shippingFee: Float)
  case class OrderDetail(orderId: String, productId: String, unitPrice: Float, quantity: Int)
  case class Product(id: String, productCode: String, productName: String, category: String, quantityPerUnit: String, listPrice: Float)

  def getLinesIterator(filePath: String): Iterator[String] = {
    val lines = Source.fromFile(filePath).getLines

    // Ignore the first line, which is just the headers
    lines.next()

    lines
  }

  def parseLineToCustomer(line: String): Customer = {
    // This pretty much shows why flat files are not ideal. I basically need to
    // know about the physical structure of the data to be able to write a program
    // about it.
    // For example, if I use the wrong integer for the column then I will misrepresent the data.
    val lineParts = line.split(',')
    Customer(
      id = lineParts(0),
      companyName = lineParts(1),
      contact = Contact(
        firstName=lineParts(2),
        lastName=lineParts(3),
        jobTitle = lineParts(4)
      ),
      city = lineParts(5),
      state = lineParts(6)
    )
  }
//  def parseLineToEmployee(line: String): Employee = {}
//  def parseLineToOrder(line: String): Employee = {}
//  def parseLineToOrderDetail(line: String): Employee = {}
//  def parseLineToProduct(line: String): Employee = {}

  //  def getAllEmployees(): List[Employee] = {}
  //  def getAllOrders(): List[Order] = {}
  //  def getAllOrderDetails(): List[OrderDetail] = {}
  //  def getAllProducts(): List[Product] = {}
  def getAllCustomers: List[Customer] = {
    getLinesIterator(customersFilePath)
      .map(parseLineToCustomer)
      .toList
  }

  //  def getEmployees(): List[Employee] = {}
  //  def getOrders(): List[Order] = {}
  //  def getOrderDetails(): List[OrderDetail] = {}
  //  def getProducts(): List[Product] = {}
  def getCustomersByIds(ids: List[String]): List[Customer] = {
    getLinesIterator(customersFilePath)
      .map(parseLineToCustomer)
      .filter { customer => ids.contains(customer.id) }
      .toList
  }

  def getCustomersByIdsInefficient(ids: List[String]): List[Customer] = {
    //  This illustrate another problem with flat files. Writing code that is optimized
    // can be tricky. This code is very similar to the getCustomersByIds function yet is slower and
    // uses way more memory.
    getLinesIterator(customersFilePath)
      .toList
      .map(parseLineToCustomer)
      .filter { customer => ids.contains(customer.id) }
  }
}

object FlatFileDatabaseScript extends App {
//  val CustomersDataFile = "data/customers"
  val CustomersDataFile = "data/customers_huge"
  val EmployeesDataFile = "data/employees"
  val OrderDetailsDataFile = "data/order_details"
  val Orders = "data/orders"
  val Products = "data/products"

  val flatFileDatabase = new FlatFileDatabase(CustomersDataFile, EmployeesDataFile, OrderDetailsDataFile, Orders, Products)

  println("Getting customers with id=26")
  flatFileDatabase.getCustomersByIds(List("26")).foreach(println)

  println("")

  println("Getting customers with id=26 (Using inefficient algorithm)")
  flatFileDatabase.getCustomersByIdsInefficient(List("26")).foreach(println)
}
