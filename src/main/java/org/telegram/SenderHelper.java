package org.telegram;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.telegram.api.Message;
import org.telegram.methods.*;
import org.telegram.services.BotLogger;

import java.io.File;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Ruben Bermudez
 * @version 1.0
 * @brief Send Helper
 * @date 20 of June of 2015
 */
public class SenderHelper {
    private static volatile BotLogger log = BotLogger.getLogger(SenderHelper.class.getName());

    public static Message SendMessage(SendMessage message, String botToken) {
        try {
            CloseableHttpClient httpclient = HttpClientBuilder.create().setSSLHostnameVerifier(new NoopHostnameVerifier()).build();
            String url = Constants.BASEURL + botToken + "/" + SendMessage.PATH;
            HttpPost httppost = new HttpPost(url);
            httppost.addHeader("Content-type", "application/x-www-form-urlencoded");
            httppost.addHeader("charset", "UTF-8");
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair(SendMessage.CHATID_FIELD, message.getChatId().toString()));
            nameValuePairs.add(new BasicNameValuePair(SendMessage.TEXT_FIELD, message.getText()));
            if (message.getDisableWebPagePreview() != null) {
                nameValuePairs.add(new BasicNameValuePair(SendMessage.DISABLEWEBPAGEPREVIEW_FIELD, message.getDisableWebPagePreview().toString()));
            }
            if (message.getReplayMarkup() != null) {
                nameValuePairs.add(new BasicNameValuePair(SendMessage.REPLYMARKUP_FIELD, message.getReplayMarkup().toJson().toString()));
            }
            if (message.getReplayToMessageId() != null) {
                nameValuePairs.add(new BasicNameValuePair(SendMessage.REPLYTOMESSAGEID_FIELD, message.getReplayToMessageId().toString()));
            }
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
            log.debug(httppost.toString());
            log.debug(nameValuePairs.toString());
            CloseableHttpResponse response = httpclient.execute(httppost);
            HttpEntity ht = response.getEntity();

            BufferedHttpEntity buf = new BufferedHttpEntity(ht);
            String responseContent = EntityUtils.toString(buf, "UTF-8");

            JSONObject jsonObject = new JSONObject(responseContent);
            if (!jsonObject.getBoolean("ok")) {
                throw new InvalidObjectException(jsonObject.toString());
            }
            JSONObject jsonMessage = jsonObject.getJSONObject("result");
            return new Message(jsonMessage);
        } catch (IOException e) {
            log.error(e);
            return null;
        }
    }

    public static void SendDocument(SendDocument sendDocument, String botToken) {
        try {
            CloseableHttpClient httpClient = HttpClients.createDefault();
            String url = Constants.BASEURL + botToken + "/" + SendDocument.PATH;
            HttpPost httppost = new HttpPost(url);

            if (sendDocument.isNewDocument()) {
                MultipartEntityBuilder builder = MultipartEntityBuilder.create();
                builder.addTextBody(SendDocument.CHATID_FIELD, sendDocument.getChatId().toString());
                builder.addBinaryBody(SendDocument.DOCUMENT_FIELD, new File(sendDocument.getDocument()), ContentType.APPLICATION_OCTET_STREAM, sendDocument.getDocumentName());
                if (sendDocument.getReplayMarkup() != null) {
                    builder.addTextBody(SendDocument.REPLYMARKUP_FIELD, sendDocument.getReplayMarkup().toJson().toString());
                }
                if (sendDocument.getReplayToMessageId() != null) {
                    builder.addTextBody(SendDocument.REPLYTOMESSAGEID_FIELD, sendDocument.getReplayToMessageId().toString());
                }
                HttpEntity multipart = builder.build();
                httppost.setEntity(multipart);
            } else {
                List<NameValuePair> nameValuePairs = new ArrayList<>();
                nameValuePairs.add(new BasicNameValuePair(SendDocument.CHATID_FIELD, sendDocument.getChatId().toString()));
                nameValuePairs.add(new BasicNameValuePair(SendDocument.DOCUMENT_FIELD, sendDocument.getDocument()));
                if (sendDocument.getReplayMarkup() != null) {
                    nameValuePairs.add(new BasicNameValuePair(SendDocument.REPLYMARKUP_FIELD, sendDocument.getReplayMarkup().toString()));
                }
                if (sendDocument.getReplayToMessageId() != null) {
                    nameValuePairs.add(new BasicNameValuePair(SendDocument.REPLYTOMESSAGEID_FIELD, sendDocument.getReplayToMessageId().toString()));
                }
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
            }

            CloseableHttpResponse response = httpClient.execute(httppost);
            if (sendDocument.isNewDocument()) {
                File fileToDelete = new File(sendDocument.getDocument());
                fileToDelete.delete();
            }
        } catch (IOException e) {
            log.error(e);
        }
    }

    public static void SendPhoto(SendPhoto sendPhoto, String botToken) {
        try {
            CloseableHttpClient httpClient = HttpClients.createDefault();
            String url = Constants.BASEURL + botToken + "/" + SendPhoto.PATH;
            HttpPost httppost = new HttpPost(url);

            if (sendPhoto.isNewPhoto()) {
                MultipartEntityBuilder builder = MultipartEntityBuilder.create();
                builder.addTextBody(SendPhoto.CHATID_FIELD, sendPhoto.getChatId().toString());
                builder.addBinaryBody(SendPhoto.PHOTO_FIELD, new File(sendPhoto.getPhoto()), ContentType.APPLICATION_OCTET_STREAM, sendPhoto.getPhotoName());
                if (sendPhoto.getReplayMarkup() != null) {
                    builder.addTextBody(SendPhoto.REPLYMARKUP_FIELD, sendPhoto.getReplayMarkup().toJson().toString());
                }
                if (sendPhoto.getReplayToMessageId() != null) {
                    builder.addTextBody(SendPhoto.REPLYTOMESSAGEID_FIELD, sendPhoto.getReplayToMessageId().toString());
                }
                if (sendPhoto.getCaption() != null) {
                    builder.addTextBody(SendPhoto.CAPTION_FIELD, sendPhoto.getCaption());
                }
                HttpEntity multipart = builder.build();
                httppost.setEntity(multipart);
            } else {
                List<NameValuePair> nameValuePairs = new ArrayList<>();
                nameValuePairs.add(new BasicNameValuePair(SendPhoto.CHATID_FIELD, sendPhoto.getChatId().toString()));
                nameValuePairs.add(new BasicNameValuePair(SendPhoto.PHOTO_FIELD, sendPhoto.getPhoto()));
                if (sendPhoto.getReplayMarkup() != null) {
                    nameValuePairs.add(new BasicNameValuePair(SendPhoto.REPLYMARKUP_FIELD, sendPhoto.getReplayMarkup().toString()));
                }
                if (sendPhoto.getReplayToMessageId() != null) {
                    nameValuePairs.add(new BasicNameValuePair(SendPhoto.REPLYTOMESSAGEID_FIELD, sendPhoto.getReplayToMessageId().toString()));
                }
                if (sendPhoto.getCaption() != null) {
                    nameValuePairs.add(new BasicNameValuePair(SendPhoto.CAPTION_FIELD, sendPhoto.getCaption()));
                }
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
            }

            CloseableHttpResponse response = httpClient.execute(httppost);
        } catch (IOException e) {
            log.error(e);
        }
    }

    public static void SendVideo(SendVideo sendVideo, String botToken) {
        try {
            CloseableHttpClient httpClient = HttpClients.createDefault();
            String url = Constants.BASEURL + botToken + "/" + SendVideo.PATH;
            HttpPost httppost = new HttpPost(url);

            if (sendVideo.isNewVideo()) {
                MultipartEntityBuilder builder = MultipartEntityBuilder.create();
                builder.addTextBody(SendVideo.CHATID_FIELD, sendVideo.getChatId().toString());
                builder.addBinaryBody(SendVideo.VIDEO_FIELD, new File(sendVideo.getVideo()), ContentType.APPLICATION_OCTET_STREAM, sendVideo.getVideoName());
                if (sendVideo.getReplayMarkup() != null) {
                    builder.addTextBody(SendVideo.REPLYMARKUP_FIELD, sendVideo.getReplayMarkup().toJson().toString());
                }
                if (sendVideo.getReplayToMessageId() != null) {
                    builder.addTextBody(SendVideo.REPLYTOMESSAGEID_FIELD, sendVideo.getReplayToMessageId().toString());
                }
                if (sendVideo.getCaption() != null) {
                    builder.addTextBody(SendVideo.CAPTION_FIELD, sendVideo.getCaption());
                }
                if (sendVideo.getDuration() != null) {
                    builder.addTextBody(SendVideo.DURATION_FIELD, sendVideo.getDuration().toString());
                }
                HttpEntity multipart = builder.build();
                httppost.setEntity(multipart);
            } else {
                List<NameValuePair> nameValuePairs = new ArrayList<>();
                nameValuePairs.add(new BasicNameValuePair(SendVideo.CHATID_FIELD, sendVideo.getChatId().toString()));
                nameValuePairs.add(new BasicNameValuePair(SendVideo.VIDEO_FIELD, sendVideo.getVideo()));
                if (sendVideo.getReplayMarkup() != null) {
                    nameValuePairs.add(new BasicNameValuePair(SendVideo.REPLYMARKUP_FIELD, sendVideo.getReplayMarkup().toString()));
                }
                if (sendVideo.getReplayToMessageId() != null) {
                    nameValuePairs.add(new BasicNameValuePair(SendVideo.REPLYTOMESSAGEID_FIELD, sendVideo.getReplayToMessageId().toString()));
                }
                if (sendVideo.getCaption() != null) {
                    nameValuePairs.add(new BasicNameValuePair(SendVideo.CAPTION_FIELD, sendVideo.getCaption()));
                }
                if (sendVideo.getDuration() != null) {
                    nameValuePairs.add(new BasicNameValuePair(SendVideo.DURATION_FIELD, sendVideo.getDuration().toString()));
                }
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
            }

            CloseableHttpResponse response = httpClient.execute(httppost);
        } catch (IOException e) {
            log.error(e);
        }
    }

    public static void sendSticker(SendSticker sendSticker, String botToken) {
        try {
            CloseableHttpClient httpClient = HttpClients.createDefault();
            String url = Constants.BASEURL + botToken + "/" + SendSticker.PATH;
            HttpPost httppost = new HttpPost(url);

            if (sendSticker.isNewSticker()) {
                MultipartEntityBuilder builder = MultipartEntityBuilder.create();
                builder.addTextBody(SendSticker.CHATID_FIELD, sendSticker.getChatId().toString());
                builder.addBinaryBody(SendSticker.STICKER_FIELD, new File(sendSticker.getSticker()), ContentType.APPLICATION_OCTET_STREAM, sendSticker.getStickerName());
                if (sendSticker.getReplayMarkup() != null) {
                    builder.addTextBody(SendSticker.REPLYMARKUP_FIELD, sendSticker.getReplayMarkup().toJson().toString());
                }
                if (sendSticker.getReplayToMessageId() != null) {
                    builder.addTextBody(SendSticker.REPLYTOMESSAGEID_FIELD, sendSticker.getReplayToMessageId().toString());
                }
                HttpEntity multipart = builder.build();
                httppost.setEntity(multipart);
            } else {
                List<NameValuePair> nameValuePairs = new ArrayList<>();
                nameValuePairs.add(new BasicNameValuePair(SendSticker.CHATID_FIELD, sendSticker.getChatId().toString()));
                nameValuePairs.add(new BasicNameValuePair(SendSticker.STICKER_FIELD, sendSticker.getSticker()));
                if (sendSticker.getReplayMarkup() != null) {
                    nameValuePairs.add(new BasicNameValuePair(SendSticker.REPLYMARKUP_FIELD, sendSticker.getReplayMarkup().toString()));
                }
                if (sendSticker.getReplayToMessageId() != null) {
                    nameValuePairs.add(new BasicNameValuePair(SendSticker.REPLYTOMESSAGEID_FIELD, sendSticker.getReplayToMessageId().toString()));
                }
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
            }

            CloseableHttpResponse response = httpClient.execute(httppost);
            if (sendSticker.isNewSticker()) {
                File fileToDelete = new File(sendSticker.getSticker());
                fileToDelete.delete();
            }
        } catch (IOException e) {
            log.error(e);
        }

    }

    public static void SendWebhook(String webHookURL, String botToken) {
        try {
            CloseableHttpClient httpclient = HttpClientBuilder.create().setSSLHostnameVerifier(new NoopHostnameVerifier()).build();
            String url = Constants.BASEURL + botToken + "/" + SetWebhook.PATH;
            HttpPost httppost = new HttpPost(url);
            httppost.addHeader("Content-type", "application/x-www-form-urlencoded");
            httppost.addHeader("charset", "UTF-8");
            List<NameValuePair> nameValuePairs = new ArrayList<>();
            nameValuePairs.add(new BasicNameValuePair(SetWebhook.URL_FIELD, webHookURL));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
            CloseableHttpResponse response = httpclient.execute(httppost);
            HttpEntity ht = response.getEntity();

            BufferedHttpEntity buf = new BufferedHttpEntity(ht);
            String responseContent = EntityUtils.toString(buf, "UTF-8");

        } catch (IOException e) {
            log.error(e);
        }

    }
}
