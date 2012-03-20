/**
 * 
 */
package ch.ethz.inf.vs.californium.proxy;

/**
 * @author Francesco Corazza
 * 
 */
public class HTTPMappingRegistry {
	// Response Codes //////////////////////////////////////////////////////////
	public static final int RESP_CLASS_SUCCESS = 2;
	public static final int RESP_CLASS_CLIENT_ERROR = 4;
	public static final int RESP_CLASS_SERVER_ERROR = 5;

	public static final int RESP_CREATED = 65;
	public static final int RESP_DELETED = 66;
	public static final int RESP_VALID = 67;
	public static final int RESP_CHANGED = 68;
	public static final int RESP_CONTENT = 69;

	public static final int RESP_BAD_REQUEST = 128;
	public static final int RESP_UNAUTHORIZED = 129;
	public static final int RESP_BAD_OPTION = 130;
	public static final int RESP_FORBIDDEN = 131;
	public static final int RESP_NOT_FOUND = 132;
	public static final int RESP_METHOD_NOT_ALLOWED = 133;
	public static final int RESP_NOT_ACCEPTABLE = 134;

	public static final int RESP_PRECONDITION_FAILED = 140;
	public static final int RESP_REQUEST_ENTITY_TOO_LARGE = 141;

	public static final int RESP_UNSUPPORTED_MEDIA_TYPE = 143;

	public static final int RESP_INTERNAL_SERVER_ERROR = 160;
	public static final int RESP_NOT_IMPLEMENTED = 161;
	public static final int RESP_BAD_GATEWAY = 162;
	public static final int RESP_SERVICE_UNAVAILABLE = 163;
	public static final int RESP_GATEWAY_TIMEOUT = 164;
	public static final int RESP_PROXYING_NOT_SUPPORTED = 165;

	// from draft-ietf-core-block-03
	public static final int RESP_REQUEST_ENTITY_INCOMPLETE = 136;

	public static int translateCode(int code) {
		switch (code) {
		case RESP_CREATED:
			return 201;
		case RESP_DELETED:
			return 204;
		case RESP_VALID:
			return 202;
		case RESP_CHANGED:
			return 205;
		case RESP_CONTENT:
			return 200;
		case RESP_BAD_REQUEST:
			return 400;
		case RESP_UNAUTHORIZED:
			return 401;
		case RESP_BAD_OPTION:
			return 402;
		case RESP_FORBIDDEN:
			return 403;
		case RESP_NOT_FOUND:
			return 404;
		case RESP_METHOD_NOT_ALLOWED:
			return 405;
		case RESP_NOT_ACCEPTABLE:
			return 406;
		case RESP_REQUEST_ENTITY_INCOMPLETE:
			return 408;
		case RESP_PRECONDITION_FAILED:
			return 412;
		case RESP_REQUEST_ENTITY_TOO_LARGE:
			return 413;
		case RESP_UNSUPPORTED_MEDIA_TYPE:
			return 415;
		case RESP_INTERNAL_SERVER_ERROR:
			return 500;
		case RESP_NOT_IMPLEMENTED:
			return 501;
		case RESP_BAD_GATEWAY:
			return 502;
		case RESP_SERVICE_UNAVAILABLE:
			return 503;
		case RESP_GATEWAY_TIMEOUT:
			return 504;
		case RESP_PROXYING_NOT_SUPPORTED:
			return 505;
		default:
			break;
		}

		return 0; // TODO
	}

	public enum COAP_METHOD {
		GET, POST, PUT, DELETE, DISCOVER, OBSERVE;
	}
}
