public class Symbol{
	public static final int UNDEFINED_POSITION = -1;
	public static final Object NO_VALUE = null;
	public static final Labels TERMINAL = null;
	public static final LexicalUnit NONTERMINAL = null;
	
	private final LexicalUnit type;
	private final Labels lbl;
	private final Object value;
	private final int line,column;

	public Symbol(LexicalUnit unit,int line,int column,Object value,Labels label){
    	this.type	= unit;
		this.line	= line+1;
		this.column	= column;
		this.value	= value;
		this.lbl 	= label;	
	}

	public Symbol(LexicalUnit unit,int line,int column,Object value){
		this(unit,line,column,value, TERMINAL);
	}
	
	public Symbol(LexicalUnit unit,int line,int column){
		this(unit,line,column,NO_VALUE, TERMINAL);
	}
	
	public Symbol(LexicalUnit unit,int line){
		this(unit,line,UNDEFINED_POSITION,NO_VALUE, TERMINAL);
	}
	
	public Symbol(LexicalUnit unit){
		this(unit,UNDEFINED_POSITION,UNDEFINED_POSITION,NO_VALUE, TERMINAL);
	}
	
	public Symbol(LexicalUnit unit,Object value){
		this(unit,UNDEFINED_POSITION,UNDEFINED_POSITION,value, TERMINAL);
	}

	public Symbol(Labels label){
		this(NONTERMINAL,UNDEFINED_POSITION,UNDEFINED_POSITION,NO_VALUE, label);
	}

	public boolean isTerminal(){
		return this.type != null;
	}
	
	public boolean isNonTerminal(){
		return this.type == null;
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

	public String toTexString(){
		if(this.isTerminal()){
			final String value	= this.value != null? this.value.toString() : "null";
			final String type	= this.type.toString();
      		return String.format(value +"-"+ type);
		}
		final String label	= this.lbl.toString();
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
