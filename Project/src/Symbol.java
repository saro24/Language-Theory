public class Symbol{
	public static final int UNDEFINED_POSITION = -1;
	public static final Object NO_VALUE = null;
	
	
	private final LexicalUnit type;
	private  Object value;
	private final int line,column;
    
	public Symbol(LexicalUnit unit,int line,int column,Object value){
    	this.type	= unit;
		this.line	= line+1;
		this.column	= column;
		this.value	= value;
	}
	
	public Symbol(LexicalUnit unit,int line,int column){
		this(unit,line,column,NO_VALUE);
	}
	
	public Symbol(LexicalUnit unit,int line){
		this(unit,line,UNDEFINED_POSITION,NO_VALUE);
	}
	
	public Symbol(LexicalUnit unit){
		this(unit,UNDEFINED_POSITION,UNDEFINED_POSITION,NO_VALUE);
	}
	
	public Symbol(LexicalUnit unit,Object value){
		this(unit,UNDEFINED_POSITION,UNDEFINED_POSITION,value);
	}

	public boolean isTerminal(){
		return this.value != null;
	}
	
	public boolean isNonTerminal(){
		return this.value == null;
	}
	
	public LexicalUnit getType(){
		return this.type;
	}
	
	public Object getValue(){
		return this.value;
	}
	
	public int getLine(){
		return this.line;
	}
	
	public int getColumn(){
		return this.column;
	}

	// Temporary method 
	public void setValue(Object value) { 
		this.value = value ; 
	}
	/**
    * Returns the value and type of the Symbol if it's terminal 
    * or the label if it's non-terminal
	*
    * @return the value and type of the Symbol if it's terminal 
    * or the label if it's non-terminal
    */
	public String toTexString(){
		if(this.isTerminal()){
			final String value	= this.value.toString();
			final String type	= this.type.toString();
      		return String.format(value +" "+ type);
		}
		final String label	= this.type.toString();
      	return String.format(label);
	}
	
	@Override
	public int hashCode(){
		final String value	= this.value != null? this.value.toString() : "null";
		final String type	= this.type  != null? this.type.toString()  : "null";
		return new String(value+"_"+type).hashCode();
	}
	
	@Override
	public String toString(){
		if(this.isTerminal()){
			final String value	= this.value != null? this.value.toString() : "null";
			final String type		= this.type  != null? this.type.toString()  : "null";
      		return String.format("token: %-15slexical unit: %s", value, type);
		}
		return "Non-terminal symbol";
	}
}
