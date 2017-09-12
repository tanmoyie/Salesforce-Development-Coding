// The Trigger will copy the values of the fields (Date, Currency, Reference, Description from Journal object into the Journal Line Item)
// Duplicating field values from Parent to Child object.
// Author: Tanmoy Das
// All right reserved @2016
// 
trigger DuplicatingField_JournalLineItem on Line_Item__c (before insert) {
    // get the Id list
    Set<Id> LineIds = new Set<Id>();
    // when inserting new Line Item, trigger will fire
    for(Line_Item__c line: (list<Line_Item__c>)Trigger.new){
        LineIds.add(line.Journal__c);
    }
    
    // Collect the field values of Journal object
    // Performing SOQL to the parent record (in our case, Journal record)
    Map<Id, Journal__c> journals = new Map<Id, Journal__c>([select id,  Journal_Date__c, Journal_Currency__c, Reference__c, Journal_Description__c,
                                                             General_Ledger_Account1__c, 
                                                             Type__c, Bank_Account__r.General_Ledger_Account__c from Journal__c where id in: LineIds]);
    
    for(Line_Item__c line: (list<Line_Item__c>)Trigger.new){  // for multiple line item records 
        Journal__c journal = journals.get(line.Journal__c);
        // update the child field values of records from parent record
        // Update Line Item from Journal
        if(journal != null)
        {
        line.Transaction_Date__c = journal.Journal_Date__c ; 
        line.Currency__c = journal.Journal_Currency__c ; 
        line.Line_Description__c = journal.Journal_Description__c ; 
        line.General_Ledger_Account__c = journal.General_Ledger_Account1__c; // need to be corrected after deployment
        line.Reference__c = journal.Reference__c ; 
        }
        if(journal.Type__c == 'Bank')
        {
        line.General_Ledger_Account__c = journal.Bank_Account__r.General_Ledger_Account__c; 
        line.Amount__c = -1*(line.Offset_Amount__c);
        }
    }
}