/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

import orders.CostOrder;
import orders.ReportCostOrder;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import android.content.Context;

import myorders.MyCostOrder;

/**
 *
 * @author p.balmasov
 */
public class Util {

    public static String[] split(String original, String separator) {
        Vector nodes = new Vector();
        // Parse nodes into vector
        int index = original.indexOf(separator);
        while (index >= 0) {
            nodes.addElement(original.substring(0, index));
            original = original.substring(index + separator.length());
            index = original.indexOf(separator);
        }
        // Get the last node
        nodes.addElement(original);

        // Create split string array
        String[] result = new String[nodes.size()];
        if (nodes.size() > 0) {
            for (int loop = 0; loop < nodes.size(); loop++) {
                result[loop] = (String) nodes.elementAt(loop);
                //System.out.println(result[loop]);
            }

        }
        return result;
    }

    public static ReportCostOrder parseReportOrder(Element item,Context context) throws DOMException, ParseException{
        Node nominalcostNode = item.getElementsByTagName("nominalcost").item(0);
        Node classNode = item.getElementsByTagName("classid").item(0);
        Node addressdepartureNode = item.getElementsByTagName("addressdeparture").item(0);
        Node orderdateNode = item.getElementsByTagName("orderdate").item(0);
        Node paymenttypeNode = item.getElementsByTagName("paymenttype").item(0);
        Node quantityNode = item.getElementsByTagName("quantity").item(0);
        Node commentNode = item.getElementsByTagName("comment").item(0);
        Node nicknameNode = item.getElementsByTagName("nickname").item(0);
        Node addressarrivalNode = item.getElementsByTagName("addressarrival").item(0);
        Node orderIdNode = item.getElementsByTagName("orderid").item(0);
        Node resultNode = item.getElementsByTagName("result").item(0);
        Node actualcostNode = item.getElementsByTagName("actualcost").item(0);
        Node drivercostNode = item.getElementsByTagName("drivercost").item(0);
        Node accepttimeNode = item.getElementsByTagName("accepttime").item(0);

        // actualcost - фактическая стоимость
        // drivercost - сколько водитель платит диспетчерской службе
        // nominalcost - рекомендуемая стоимость

        // Node invitationNode = item.getElementsByTagName("invitationtime").item(0);

        Date accepttime = null;
        String actualcost = null;
        String drivercost = null;
        String nominalcost = null;
        Integer carClass = 0;
        String addressdeparture = null;
        Date orderdate = null;
        Integer paymenttype = null;
        Integer quantity = null;
        String comment = null;
        String nickname = null;
        String addressarrival = null;
        String orderId = null;
        String result = null;
        // Date invitationtime = null;

        // if(departuretime==null)
        // //TODO:не предварительный
        // else
        // //TODO:предварительный

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");

        if (!classNode.getTextContent().equalsIgnoreCase(""))
            carClass = Integer.valueOf(classNode.getTextContent());

        if (!nominalcostNode.getTextContent().equalsIgnoreCase(""))
            nominalcost = nominalcostNode.getTextContent();

        if (!addressdepartureNode.getTextContent().equalsIgnoreCase(""))
            addressdeparture = addressdepartureNode.getTextContent();

        if (!addressarrivalNode.getTextContent().equalsIgnoreCase(""))
            addressarrival = addressarrivalNode.getTextContent();

        if (!paymenttypeNode.getTextContent().equalsIgnoreCase(""))
            paymenttype = Integer.parseInt(paymenttypeNode.getTextContent());

        if (!orderdateNode.getTextContent().equalsIgnoreCase(""))
            orderdate = format.parse(orderdateNode.getTextContent());

        if (!commentNode.getTextContent().equalsIgnoreCase(""))
            comment = commentNode.getTextContent();

        if (!orderIdNode.getTextContent().equalsIgnoreCase(""))
            orderId = orderIdNode.getTextContent();

        if (!resultNode.getTextContent().equalsIgnoreCase(""))
            result = resultNode.getTextContent();

        if (!drivercostNode.getTextContent().equalsIgnoreCase(""))
            drivercost = drivercostNode.getTextContent();

        if (!actualcostNode.getTextContent().equalsIgnoreCase(""))
            actualcost = actualcostNode.getTextContent();

        if (!accepttimeNode.getTextContent().equalsIgnoreCase(""))
            accepttime = format.parse(accepttimeNode.getTextContent());

        // if (!invitationNode.getTextContent().equalsIgnoreCase(""))
        // invitationtime = format.parse(invitationNode.getTextContent());

        ReportCostOrder order = new ReportCostOrder(context, orderId, nominalcost, addressdeparture, carClass, comment,
                addressarrival, paymenttype, orderdate, result, drivercost, actualcost, accepttime);

        if (!nicknameNode.getTextContent().equalsIgnoreCase("")) {
            nickname = nicknameNode.getTextContent();

            if (!quantityNode.getTextContent().equalsIgnoreCase(""))
                quantity = Integer.parseInt(quantityNode.getTextContent());
            order.setAbonent(nickname);
            order.setRides(quantity);
        }
        return order;
    }


    public static MyCostOrder parseMyCostOrder(Element item,Context context){
        return parseMyCostOrder(item, context);
    }

    public static MyCostOrder parseMyCostOrder(Element item,Context context,Node servertimeNode) throws DOMException, ParseException{

        Node nominalcostNode = item.getElementsByTagName("nominalcost").item(0);
        Node classNode = item.getElementsByTagName("classid").item(0);
        Node addressdepartureNode = item.getElementsByTagName("addressdeparture").item(0);
        Node departuretimeNode = item.getElementsByTagName("departuretime").item(0);
        Node paymenttypeNode = item.getElementsByTagName("paymenttype").item(0);
        Node quantityNode = item.getElementsByTagName("quantity").item(0);
        Node commentNode = item.getElementsByTagName("comment").item(0);
        Node nicknameNode = item.getElementsByTagName("nickname").item(0);
        Node addressarrivalNode = item.getElementsByTagName("addressarrival").item(0);
        Node orderIdNode = item.getElementsByTagName("orderid").item(0);
        Node invitationNode = item.getElementsByTagName("invitationtime").item(0);
        Node accepttimeNode = item.getElementsByTagName("accepttime").item(0);
        Node driverstateNode = item.getElementsByTagName("driverstate").item(0);
        Node orderedtimeNode = item.getElementsByTagName("orderedtime").item(0);

        Integer driverstate = null;
        Date accepttime = null;
        String nominalcost = null;
        Integer carClass = 0;
        String addressdeparture = null;
        Date departuretime = null;
        Integer paymenttype = null;
        Integer quantity = null;
        String comment = null;
        String nickname = null;
        String addressarrival = null;
        String orderId = null;
        Date invitationtime = null;
        Date servertime = null;
        Date orderedtime = null;


        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");

        if(servertimeNode!=null)
        if (!servertimeNode.getTextContent().equalsIgnoreCase(""))
            servertime = format.parse(servertimeNode.getTextContent());

        if (!driverstateNode.getTextContent().equalsIgnoreCase(""))
            driverstate = Integer.valueOf(driverstateNode.getTextContent());

        if (!classNode.getTextContent().equalsIgnoreCase(""))
            carClass = Integer.valueOf(classNode.getTextContent());

        if (!nominalcostNode.getTextContent().equalsIgnoreCase(""))
            nominalcost = nominalcostNode.getTextContent();

        if (!addressdepartureNode.getTextContent().equalsIgnoreCase(""))
            addressdeparture = addressdepartureNode.getTextContent();

        if (!addressarrivalNode.getTextContent().equalsIgnoreCase(""))
            addressarrival = addressarrivalNode.getTextContent();

        if (!paymenttypeNode.getTextContent().equalsIgnoreCase(""))
            paymenttype = Integer.parseInt(paymenttypeNode.getTextContent());

        if (!departuretimeNode.getTextContent().equalsIgnoreCase(""))
            departuretime = format.parse(departuretimeNode.getTextContent());

        if (!commentNode.getTextContent().equalsIgnoreCase(""))
            comment = commentNode.getTextContent();

        if (!orderIdNode.getTextContent().equalsIgnoreCase(""))
            orderId = orderIdNode.getTextContent();

        if (!invitationNode.getTextContent().equalsIgnoreCase(""))
            invitationtime = format.parse(invitationNode.getTextContent());

        if (!accepttimeNode.getTextContent().equalsIgnoreCase(""))
            accepttime = format.parse(accepttimeNode.getTextContent());

        if (!orderedtimeNode.getTextContent().equalsIgnoreCase(""))
            orderedtime = format.parse(orderedtimeNode.getTextContent());

        MyCostOrder order = new MyCostOrder(context, orderId, nominalcost, addressdeparture, carClass, comment,
                addressarrival, paymenttype, invitationtime, departuretime, accepttime, driverstate,
                servertime, orderedtime);

        if (!nicknameNode.getTextContent().equalsIgnoreCase("")) {
            nickname = nicknameNode.getTextContent();

            if (!quantityNode.getTextContent().equalsIgnoreCase(""))
                quantity = Integer.parseInt(quantityNode.getTextContent());
            order.setAbonent(nickname);
            order.setRides(quantity);
        }
        return order;
    }

    public static CostOrder parseCostOrder(Element item,Context context) throws DOMException, ParseException{
        Node nominalcostNode = item.getElementsByTagName("nominalcost").item(0);
        Node classNode = item.getElementsByTagName("classid").item(0);
        Node addressdepartureNode = item.getElementsByTagName("addressdeparture").item(0);
        Node departuretimeNode = item.getElementsByTagName("departuretime").item(0);
        Node paymenttypeNode = item.getElementsByTagName("paymenttype").item(0);
        Node quantityNode = item.getElementsByTagName("quantity").item(0);
        Node commentNode = item.getElementsByTagName("comment").item(0);
        Node nicknameNode = item.getElementsByTagName("nickname").item(0);
        Node addressarrivalNode = item.getElementsByTagName("addressarrival").item(0);
        Node orderIdNode = item.getElementsByTagName("orderid").item(0);

        String nominalcost = null;
        Integer carClass = 0;
        String addressdeparture = null;
        Date departuretime = null;
        Integer paymenttype = null;
        Integer quantity = null;
        String comment = null;
        String nickname = null;
        String addressarrival = null;
        String orderId = null;

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");

        if (!classNode.getTextContent().equalsIgnoreCase(""))
            carClass = Integer.valueOf(classNode.getTextContent());

        if (!nominalcostNode.getTextContent().equalsIgnoreCase(""))
            nominalcost = nominalcostNode.getTextContent();

        if (!addressdepartureNode.getTextContent().equalsIgnoreCase(""))
            addressdeparture = addressdepartureNode.getTextContent();

        if (!addressarrivalNode.getTextContent().equalsIgnoreCase(""))
            addressarrival = addressarrivalNode.getTextContent();

        if (!paymenttypeNode.getTextContent().equalsIgnoreCase(""))
            paymenttype = Integer.parseInt(paymenttypeNode.getTextContent());

        if (!departuretimeNode.getTextContent().equalsIgnoreCase(""))
            departuretime = format.parse(departuretimeNode.getTextContent());

        if (!commentNode.getTextContent().equalsIgnoreCase(""))
            comment = commentNode.getTextContent();

        if (!orderIdNode.getTextContent().equalsIgnoreCase(""))
            orderId = orderIdNode.getTextContent();

        CostOrder order = new CostOrder(context, orderId, nominalcost, addressdeparture, carClass, comment,
                addressarrival, paymenttype, departuretime);

        if (!nicknameNode.getTextContent().equalsIgnoreCase("")) {
            nickname = nicknameNode.getTextContent();

            if (!quantityNode.getTextContent().equalsIgnoreCase(""))
                quantity = Integer.parseInt(quantityNode.getTextContent());
            order.setAbonent(nickname);
            order.setRides(quantity);
        }
        return order;
    }
}
