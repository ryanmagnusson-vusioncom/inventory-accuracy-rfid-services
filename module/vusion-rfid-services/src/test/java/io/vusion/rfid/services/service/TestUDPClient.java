package io.vusion.rfid.services.service;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

@NoArgsConstructor
public class TestUDPClient {
    @Setter @Getter
    private String address;


    private DatagramSocket socket;
    private InetAddress    inetAddress;

    public TestUDPClient(String address) {
        this.address = address;
    }

    private byte[] buf;

    @SneakyThrows({SocketException.class, UnknownHostException.class})
    public TestUDPClient open() {
        if (inetAddress == null) {
            inetAddress = InetAddress.getByName(address);
        }

        if (socket == null) {
            socket = new DatagramSocket();
        }
        return this;
    }

    //@SneakyThrows({ UnsupportedEncodingException.class, SocketException.class, IOException.class })
    public void sendMessageAsASCIIBytes(String msg) {
        try {
//            buf = msg.getBytes("US-ASCII");
            buf = msg.getBytes();
            open();
            DatagramPacket packet
                    = new DatagramPacket(buf, buf.length, inetAddress, 4445);
            socket.send(packet);
            //packet = new DatagramPacket(buf, buf.length);
            //socket.receive(packet);
            //return new String(packet.getData(), 0, packet.getLength());
        } catch (IOException e) {
            throw new RuntimeException(ExceptionUtils.getMessage(e), e);
        }

    }

    public void close() {
        socket.close();
    }
}
