/*
 * JBILLING CONFIDENTIAL
 * _____________________
 *
 * [2003] - [2012] Enterprise jBilling Software Ltd.
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Enterprise jBilling Software.
 * The intellectual and technical concepts contained
 * herein are proprietary to Enterprise jBilling Software
 * and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden.
 */
package com.sapienter.jbilling.server.item.tasks;

import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.drools.KnowledgeBase;
import org.drools.runtime.StatelessKnowledgeSession;
import org.jopendocument.dom.spreadsheet.Sheet;
import org.jopendocument.dom.spreadsheet.SpreadSheet;

import com.sapienter.jbilling.server.item.ItemBL;
import com.sapienter.jbilling.server.item.PricingField;
import com.sapienter.jbilling.server.item.db.ItemDTO;
import com.sapienter.jbilling.server.order.OrderBL;
import com.sapienter.jbilling.server.order.db.OrderDTO;
import com.sapienter.jbilling.server.order.db.OrderLineDTO;
import com.sapienter.jbilling.server.pluggableTask.PluggableTask;
import com.sapienter.jbilling.server.pluggableTask.TaskException;
import com.sapienter.jbilling.server.user.ContactBL;
import com.sapienter.jbilling.server.user.ContactDTOEx;
import com.sapienter.jbilling.server.user.UserDTOEx;
import com.sapienter.jbilling.server.user.contact.db.ContactFieldDTO;
import com.sapienter.jbilling.server.util.DTOFactory;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;

public class DataloyPricingTask extends PluggableTask implements IPricing {
    
    private static final Logger LOG = Logger.getLogger(RulesPricingTask.class);

    public BigDecimal getPrice(Integer itemId, BigDecimal quantity, Integer userId, Integer currencyId,
            List<PricingField> fields, BigDecimal defaultPrice, OrderDTO pricingOrder, boolean singlePurchase)
            throws TaskException {
        // now we have the line with good defaults, the order and the item
        // These have to be visible to the rules
        KnowledgeBase knowledgeBase;
        try {
            knowledgeBase = readKnowledgeBase();
        } catch (Exception e) {
            throw new TaskException(e);
        }
        StatelessKnowledgeSession mySession = knowledgeBase.newStatelessKnowledgeSession();
        List<Object> rulesMemoryContext = new ArrayList<Object>();
        
        PricingManager manager = new PricingManager(itemId, userId, currencyId, defaultPrice);
        mySession.setGlobal("manager", manager);
        
        ItemBL ibl=new ItemBL(itemId);
        
        ItemDTO idto=ibl.getEntity();
        String description=idto.getDescription();
        
        String payPlan=pricingOrder.getPayPlan();
        
        
        BigDecimal value=defaultPrice;
		if(payPlan!=null){
			try {
				File file = new File("resources/pay_plans/"+payPlan+"_"+description+".ods");
				System.out.println(file.toPath());
				Sheet sheet=null;
				sheet = SpreadSheet.createFromFile(file).getSheet(""+2013);
				value=(BigDecimal) sheet.getCellAt("B"+quantity.intValue()).getValue();
				BigDecimal amount=(BigDecimal) sheet.getCellAt("C"+quantity.intValue()).getValue();
				System.out.println(value+" "+amount);
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		manager.setPrice(value);
		
		
		
		
		mySession.execute(rulesMemoryContext);

        return manager.getPrice();
    }
}
