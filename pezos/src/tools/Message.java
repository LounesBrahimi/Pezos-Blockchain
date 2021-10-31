package tools;

public enum Message {
	GET_CURRENT_HEAD(1),
	CURRENT_HEAD(2),// <block>
	GET_BLOCK(3), // <level>
	BLOCK(4), // <block>
	GET_BLOCK_OPERATIONS(5), // <level>
	BLOCK_OPERATIONS(6),// <op list>
	GET_BLOCK_STATE(7), // <level>
	BLOCK_STATE(8), // <state>
	INJECT_OPERATION(9); // <op>

	private int tag;
	private int level;

	Message(int tag) {
		this.tag   = tag;
	}

	Message(int tag, int level) {
		this.tag   = tag;
	    this.level = level;
	}
}
