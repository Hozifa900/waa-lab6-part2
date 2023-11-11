package websocket;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.ArrayList;
import java.util.List;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

@Component
public class SocketTextHandler extends TextWebSocketHandler {

	List<WebSocketSession> sessions = new ArrayList<WebSocketSession>();

	@Override
	public void handleTextMessage(WebSocketSession session, TextMessage message)
			throws Exception {
		System.out.println("got message" + message);
		double d = evaluateExpression(message.getPayload());
		for (WebSocketSession theSession : sessions) {
			if (theSession.isOpen())
				theSession.sendMessage(new TextMessage(message.getPayload() + " = " + d));
		}
	}

	public static double evaluateExpression(String expression) {
		try {
			String[] parts = expression.split("[\\+\\-\\*\\/]");
			if (parts.length != 2) {
				throw new ArithmeticException("Invalid expression format");
			}

			double x = Double.parseDouble(parts[0]);
			double y = Double.parseDouble(parts[1]);
			char operator = findOperator(expression);

			switch (operator) {
				case '+':
					return x + y;
				case '-':
					return x - y;
				case '*':
					return x * y;
				case '/':
					if (y == 0) {
						throw new ArithmeticException("Division by zero");
					}
					return x / y;
				default:
					throw new ArithmeticException("Unsupported operator");
			}
		} catch (NumberFormatException e) {
			throw new ArithmeticException("Invalid number format");
		}
	}

	public static char findOperator(String expression) {
		for (char c : expression.toCharArray()) {
			if (c == '+' || c == '-' || c == '*' || c == '/') {
				return c;
			}
		}
		throw new ArithmeticException("Operator not found");
	}

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		super.afterConnectionEstablished(session);
		System.out.println("Connected");
		sessions.add(session);
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		super.afterConnectionClosed(session, status);
		sessions.remove(session);
		System.out.println("Closed");
	}

}