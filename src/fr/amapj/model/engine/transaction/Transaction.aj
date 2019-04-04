package fr.amapj.model.engine.transaction;


aspect Transaction {


	pointcut read() : (execution(@DbRead * *(..)));


	before(): read()
	{
		TransactionHelper.mainInstance.start_read();
	}

	after() returning: read()
	{
		TransactionHelper.mainInstance.stop_read(false);
	}
	
	
	after() throwing: read()
	{
		TransactionHelper.mainInstance.stop_read(true);
	}

	
	pointcut write() : (execution(@DbWrite * *(..)));
	
	
	before(): write()
	{
		TransactionHelper.mainInstance.start_write();
	}

	after() returning: write()
	{
		TransactionHelper.mainInstance.stop_write(false);
	}
	
	after() throwing : write()
	{
		TransactionHelper.mainInstance.stop_write(true);
	}

	
	
	

	
	

}