package com.github.tsavo.apoimatic.net;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class DNS {

	public static String getIP(InetAddress anAddress) {
		return anAddress.getHostAddress();
	}

	public static String getIP(String aDomain) throws UnknownHostException {
		return getIP(InetAddress.getByName(aDomain));
	}

	public static String getDomain(InetAddress anAddress) {
		return getDomain(anAddress.getHostAddress());
	}

	public static String getDomain(String anIP) {
		String retVal = null;
		final String[] bytes = anIP.split("\\.");
		if (bytes.length == 4) {
			try {
				final java.util.Hashtable<String, String> env = new java.util.Hashtable<String, String>();
				env.put("java.naming.factory.initial", "com.sun.jndi.dns.DnsContextFactory");
				final javax.naming.directory.DirContext ctx = new javax.naming.directory.InitialDirContext(env);
				final String reverseDnsDomain = bytes[3] + "." + bytes[2] + "." + bytes[1] + "." + bytes[0] + ".in-addr.arpa";
				final javax.naming.directory.Attributes attrs = ctx.getAttributes(reverseDnsDomain, new String[] { "PTR", });
				for (final javax.naming.NamingEnumeration<? extends javax.naming.directory.Attribute> ae = attrs.getAll(); ae.hasMoreElements();) {
					final javax.naming.directory.Attribute attr = ae.next();
					final String attrId = attr.getID();
					for (final java.util.Enumeration<?> vals = attr.getAll(); vals.hasMoreElements();) {
						String value = vals.nextElement().toString();
						// System.out.println(attrId + ": " + value);

						if ("PTR".equals(attrId)) {
							final int len = value.length();
							if (value.charAt(len - 1) == '.') {
								// Strip out trailing period
								value = value.substring(0, len - 1);
							}
							retVal = value;
						}
					}
				}
				ctx.close();
			} catch (final javax.naming.NamingException e) {

			}
		}

		if (null == retVal) {
			try {
				retVal = InetAddress.getByName(anIP).getCanonicalHostName();
			} catch (final UnknownHostException e1) {
				retVal = anIP;
			}
		}
		return retVal;
	}
}
