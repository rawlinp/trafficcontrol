/*
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.comcast.cdn.traffic_control.traffic_router.core.secure

import kotlin.Throws
import java.lang.Exception
import com.comcast.cdn.traffic_control.traffic_router.core.ds.SteeringRegistry
import com.comcast.cdn.traffic_control.traffic_router.core.ds.SteeringTarget
import org.powermock.core.classloader.annotations.PrepareForTest
import com.comcast.cdn.traffic_control.traffic_router.core.ds.DeliveryService
import org.junit.runner.RunWith
import org.powermock.modules.junit4.PowerMockRunner
import com.comcast.cdn.traffic_control.traffic_router.core.ds.DeliveryServiceMatcher
import com.comcast.cdn.traffic_control.traffic_router.core.request.HTTPRequest
import com.comcast.cdn.traffic_control.traffic_router.geolocation.Geolocation
import com.comcast.cdn.traffic_control.traffic_router.core.ds.SteeringResult
import com.comcast.cdn.traffic_control.traffic_router.core.ds.SteeringGeolocationComparator
import org.junit.Before
import com.comcast.cdn.traffic_control.traffic_router.shared.ZoneTestRecords
import org.xbill.DNS.RRset
import com.comcast.cdn.traffic_control.traffic_router.core.dns.RRSetsBuilder
import java.util.concurrent.ExecutorService
import java.util.concurrent.LinkedBlockingQueue
import com.comcast.cdn.traffic_control.traffic_router.core.dns.protocol.TCP
import java.net.InetAddress
import java.io.ByteArrayInputStream
import java.net.ServerSocket
import java.util.concurrent.BlockingQueue
import java.lang.Runnable
import com.comcast.cdn.traffic_control.traffic_router.core.dns.protocol.TCP.TCPSocketHandler
import org.xbill.DNS.DClass
import com.comcast.cdn.traffic_control.traffic_router.core.dns.DNSAccessRecord
import org.xbill.DNS.Rcode
import java.lang.RuntimeException
import org.powermock.api.mockito.PowerMockito
import java.net.DatagramSocket
import com.comcast.cdn.traffic_control.traffic_router.core.dns.protocol.UDP
import org.xbill.DNS.OPTRecord
import java.util.concurrent.atomic.AtomicInteger
import com.comcast.cdn.traffic_control.traffic_router.core.dns.protocol.UDP.UDPPacketHandler
import com.comcast.cdn.traffic_control.traffic_router.core.dns.protocol.AbstractProtocolTest.FakeAbstractProtocol
import com.comcast.cdn.traffic_control.traffic_router.core.dns.DNSAccessEventBuilder
import java.lang.System
import com.comcast.cdn.traffic_control.traffic_router.core.dns.protocol.AbstractProtocolTest
import java.net.Inet4Address
import org.xbill.DNS.ARecord
import org.xbill.DNS.WireParseException
import com.comcast.cdn.traffic_control.traffic_router.core.router.TrafficRouterManager
import com.comcast.cdn.traffic_control.traffic_router.core.router.TrafficRouter
import com.comcast.cdn.traffic_control.traffic_router.core.edge.CacheRegister
import org.xbill.DNS.NSRecord
import org.xbill.DNS.SOARecord
import org.xbill.DNS.ClientSubnetOption
import com.comcast.cdn.traffic_control.traffic_router.core.util.JsonUtils
import org.xbill.DNS.EDNSOption
import java.util.HashSet
import com.comcast.cdn.traffic_control.traffic_router.core.util.IntegrationTest
import java.util.HashMap
import com.comcast.cdn.traffic_control.traffic_router.core.dns.ZoneManagerTest
import com.google.common.net.InetAddresses
import org.xbill.DNS.TextParseException
import com.comcast.cdn.traffic_control.traffic_router.core.dns.ZoneManager
import com.comcast.cdn.traffic_control.traffic_router.core.edge.CacheLocation
import com.comcast.cdn.traffic_control.traffic_router.core.edge.Node.IPVersions
import com.google.common.cache.CacheStats
import org.junit.BeforeClass
import java.nio.file.Paths
import com.comcast.cdn.traffic_control.traffic_router.core.TestBase
import com.comcast.cdn.traffic_control.traffic_router.core.dns.DNSException
import com.comcast.cdn.traffic_control.traffic_router.core.dns.NameServerMain
import com.comcast.cdn.traffic_control.traffic_router.core.dns.ZoneSignerImpl
import com.comcast.cdn.traffic_control.traffic_router.core.dns.DnsSecKeyPair
import com.comcast.cdn.traffic_control.traffic_router.core.dns.DnsSecKeyPairImpl
import org.xbill.DNS.DNSKEYRecord
import java.security.PrivateKey
import org.xbill.DNS.RRSIGRecord
import com.comcast.cdn.traffic_control.traffic_router.core.dns.ZoneSignerImplTest.IsRRsetTypeA
import com.comcast.cdn.traffic_control.traffic_router.core.dns.ZoneSignerImplTest.IsRRsetTypeNSEC
import java.util.concurrent.ConcurrentMap
import com.comcast.cdn.traffic_control.traffic_router.core.dns.RRSIGCacheKey
import com.comcast.cdn.traffic_control.traffic_router.core.dns.RRsetKey
import java.util.concurrent.ConcurrentHashMap
import com.comcast.cdn.traffic_control.traffic_router.core.dns.SignatureManager
import com.comcast.cdn.traffic_control.traffic_router.core.util.TrafficOpsUtils
import com.comcast.cdn.traffic_control.traffic_router.core.router.StatTracker
import org.xbill.DNS.SetResponse
import com.comcast.cdn.traffic_control.traffic_router.core.router.StatTracker.Track.ResultType
import org.xbill.DNS.NSECRecord
import com.comcast.cdn.traffic_control.traffic_router.core.router.StatTracker.Track.ResultDetails
import com.comcast.cdn.traffic_control.traffic_router.core.loc.GeolocationDatabaseUpdater
import com.comcast.cdn.traffic_control.traffic_router.core.loc.MaxmindGeolocationService
import com.comcast.cdn.traffic_control.traffic_router.core.loc.GeoTest
import com.comcast.cdn.traffic_control.traffic_router.core.loc.AnonymousIp
import com.comcast.cdn.traffic_control.traffic_router.core.loc.AnonymousIpDatabaseService
import com.comcast.cdn.traffic_control.traffic_router.core.loc.AnonymousIpWhitelist
import java.io.IOException
import com.comcast.cdn.traffic_control.traffic_router.core.loc.NetworkNode
import com.comcast.cdn.traffic_control.traffic_router.core.loc.NetworkNodeTest
import com.comcast.cdn.traffic_control.traffic_router.core.loc.NetworkNodeException
import com.comcast.cdn.traffic_control.traffic_router.core.loc.RegionalGeo
import com.comcast.cdn.traffic_control.traffic_router.core.loc.RegionalGeoResult
import com.comcast.cdn.traffic_control.traffic_router.core.loc.RegionalGeoResult.RegionalGeoResultType
import com.comcast.cdn.traffic_control.traffic_router.geolocation.GeolocationException
import java.net.MalformedURLException
import com.comcast.cdn.traffic_control.traffic_router.core.router.HTTPRouteResult
import com.comcast.cdn.traffic_control.traffic_router.core.edge.Cache.DeliveryServiceReference
import com.comcast.cdn.traffic_control.traffic_router.core.edge.CacheLocation.LocalizationMethod
import com.comcast.cdn.traffic_control.traffic_router.core.loc.MaxmindGeoIP2Test
import com.comcast.cdn.traffic_control.traffic_router.core.loc.RegionalGeoRule.PostalsType
import com.comcast.cdn.traffic_control.traffic_router.core.loc.RegionalGeoRule
import com.comcast.cdn.traffic_control.traffic_router.core.loc.NetworkNode.SuperNode
import com.comcast.cdn.traffic_control.traffic_router.core.loc.RegionalGeoCoordinateRange
import com.comcast.cdn.traffic_control.traffic_router.core.loc.Federation
import com.comcast.cdn.traffic_control.traffic_router.core.util.CidrAddress
import com.comcast.cdn.traffic_control.traffic_router.core.util.ComparableTreeSet
import com.comcast.cdn.traffic_control.traffic_router.core.loc.FederationMapping
import com.comcast.cdn.traffic_control.traffic_router.core.loc.FederationRegistry
import com.comcast.cdn.traffic_control.traffic_router.core.edge.InetRecord
import com.comcast.cdn.traffic_control.traffic_router.core.loc.FederationsBuilder
import com.comcast.cdn.traffic_control.traffic_router.core.util.JsonUtilsException
import com.comcast.cdn.traffic_control.traffic_router.core.loc.AbstractServiceUpdater
import java.nio.file.Path
import com.comcast.cdn.traffic_control.traffic_router.core.loc.AbstractServiceUpdaterTest.Updater
import com.comcast.cdn.traffic_control.traffic_router.core.loc.FederationMappingBuilder
import com.maxmind.geoip2.DatabaseReader
import com.maxmind.geoip2.model.CityResponse
import com.comcast.cdn.traffic_control.traffic_router.core.loc.AnonymousIpDatabaseServiceTest
import com.maxmind.geoip2.model.AnonymousIpResponse
import com.maxmind.geoip2.exception.GeoIp2Exception
import java.util.TreeSet
import com.comcast.cdn.traffic_control.traffic_router.core.http.HTTPAccessEventBuilder
import com.comcast.cdn.traffic_control.traffic_router.core.http.HTTPAccessRecord
import java.lang.StringBuffer
import com.comcast.cdn.traffic_control.traffic_router.core.util.Fetcher
import java.io.InputStreamReader
import org.powermock.core.classloader.annotations.PowerMockIgnore
import java.io.BufferedReader
import com.comcast.cdn.traffic_control.traffic_router.core.loc.FederationsWatcher
import com.comcast.cdn.traffic_control.traffic_router.core.ds.SteeringWatcher
import java.lang.InterruptedException
import com.comcast.cdn.traffic_control.traffic_router.core.util.AbstractResourceWatcherTest
import com.comcast.cdn.traffic_control.traffic_router.core.util.ComparableStringByLength
import com.comcast.cdn.traffic_control.traffic_router.core.config.ConfigHandler
import java.lang.Void
import com.comcast.cdn.traffic_control.traffic_router.shared.CertificateData
import com.comcast.cdn.traffic_control.traffic_router.core.config.CertificateChecker
import com.comcast.cdn.traffic_control.traffic_router.core.hash.ConsistentHasher
import com.comcast.cdn.traffic_control.traffic_router.core.ds.Dispersion
import com.comcast.cdn.traffic_control.traffic_router.core.router.DNSRouteResult
import com.comcast.cdn.traffic_control.traffic_router.core.request.DNSRequest
import com.comcast.cdn.traffic_control.traffic_router.core.loc.NetworkUpdater
import com.comcast.cdn.traffic_control.traffic_router.core.router.StatelessTrafficRouterTest
import com.comcast.cdn.traffic_control.traffic_router.core.router.LocationComparator
import org.bouncycastle.jce.provider.BouncyCastleProvider
import com.comcast.cdn.traffic_control.traffic_router.secure.Pkcs1
import java.security.spec.KeySpec
import java.security.spec.PKCS8EncodedKeySpec
import com.comcast.cdn.traffic_control.traffic_router.core.secure.CertificatesClient
import com.comcast.cdn.traffic_control.traffic_router.core.hash.NumberSearcher
import com.comcast.cdn.traffic_control.traffic_router.core.hash.DefaultHashable
import com.comcast.cdn.traffic_control.traffic_router.core.hash.MD5HashFunction
import com.comcast.cdn.traffic_control.traffic_router.core.hash.Hashable
import com.comcast.cdn.traffic_control.traffic_router.core.util.ExternalTest
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.catalina.LifecycleException
import org.apache.http.impl.client.HttpClientBuilder
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.CloseableHttpResponse
import org.apache.http.util.EntityUtils
import org.junit.FixMethodOrder
import org.junit.runners.MethodSorters
import java.security.KeyStore
import com.comcast.cdn.traffic_control.traffic_router.core.external.RouterTest.ClientSslSocketFactory
import com.comcast.cdn.traffic_control.traffic_router.core.external.RouterTest.TestHostnameVerifier
import org.xbill.DNS.SimpleResolver
import javax.net.ssl.SSLHandshakeException
import org.apache.http.client.methods.HttpPost
import org.apache.http.client.methods.HttpHead
import org.apache.http.conn.ssl.SSLConnectionSocketFactory
import org.apache.http.conn.ssl.TrustSelfSignedStrategy
import javax.net.ssl.SNIHostName
import javax.net.ssl.SNIServerName
import javax.net.ssl.SSLParameters
import javax.net.ssl.SSLSession
import org.hamcrest.number.IsCloseTo
import com.comcast.cdn.traffic_control.traffic_router.core.http.RouterFilter
import java.net.InetSocketAddress
import org.junit.runners.Suite
import org.junit.runners.Suite.SuiteClasses
import com.comcast.cdn.traffic_control.traffic_router.core.external.SteeringTest
import com.comcast.cdn.traffic_control.traffic_router.core.external.ConsistentHashTest
import com.comcast.cdn.traffic_control.traffic_router.core.external.DeliveryServicesTest
import com.comcast.cdn.traffic_control.traffic_router.core.external.LocationsTest
import com.comcast.cdn.traffic_control.traffic_router.core.external.RouterTest
import com.comcast.cdn.traffic_control.traffic_router.core.external.StatsTest
import com.comcast.cdn.traffic_control.traffic_router.core.external.ZonesTest
import com.comcast.cdn.traffic_control.traffic_router.core.CatalinaTrafficRouter
import com.comcast.cdn.traffic_control.traffic_router.core.external.HttpDataServer
import com.comcast.cdn.traffic_control.traffic_router.core.external.ExternalTestSuite
import org.apache.log4j.ConsoleAppender
import org.apache.log4j.PatternLayout
import org.junit.AfterClass
import java.nio.file.SimpleFileVisitor
import java.nio.file.attribute.BasicFileAttributes
import java.nio.file.FileVisitResult
import org.hamcrest.number.OrderingComparison
import javax.management.MBeanServer
import javax.management.ObjectName
import com.comcast.cdn.traffic_control.traffic_router.shared.DeliveryServiceCertificatesMBean
import com.comcast.cdn.traffic_control.traffic_router.shared.DeliveryServiceCertificates
import org.springframework.context.support.FileSystemXmlApplicationContext
import kotlin.jvm.JvmStatic
import org.apache.catalina.startup.Catalina
import org.apache.catalina.core.StandardService
import java.util.stream.Collectors
import org.apache.catalina.core.StandardHost
import org.apache.catalina.core.StandardContext
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.junit.Test

class CertificatesClientTest {
    @Test
    fun itUsesBase64MimeDecoder() {
        val certificatesClient = CertificatesClient()
        MatcherAssert.assertThat(
            certificatesClient.doubleDecode(MIME_ENCODED_HTTPS_TEST_CERT).size,
            Matchers.equalTo(1)
        )
    }

    @Test
    fun itDecodesCertificateChainBundle() {
        val certificatesClient = CertificatesClient()
        val encodedCerts = certificatesClient.doubleDecode(MIME_ENCODED_CERT_CHAIN)
        MatcherAssert.assertThat(encodedCerts.size, Matchers.equalTo(3))
    }

    // This is just a self signed test cert for testing
    var MIME_ENCODED_HTTPS_TEST_CERT = """
           LS0tLS1CRUdJTiBDRVJUSUZJQ0FURS0tLS0tCk1JSUQ4akNDQXRvQ0NRQ25Oelk2L3pZUGNEQU5C
           Z2txaGtpRzl3MEJBUXNGQURDQnVqRUxNQWtHQTFVRUJoTUNWVk14RVRBUEJnTlYKQkFnTUNFTnZi
           Rzl5WVdSdk1ROHdEUVlEVlFRSERBWkVaVzUyWlhJeEZEQVNCZ05WQkFvTUMwVjRZVzF3YkdVZ1NX
           NWpNUmt3RndZRApWUVFMREJCVmJtbGpiM0p1SUZSeVlXbHVaWEp6TVNnd0pnWURWUVFEREI4cUxt
           aDBkSEJ6TFhSbGMzUXVkR2hsWTJSdUxtVjRZVzF3CmJHVXVZMjl0TVN3d0tnWUpLb1pJaHZjTkFR
           a0JGaDF2Y0dWeVlYUnBiMjV6UUhSb1pXTmtiaTVsZUdGdGNHeGxMbU52YlRBZUZ3MHgKTmpBMk16
           QXlNRFUzTVRoYUZ3MHlOakEyTWpneU1EVTNNVGhhTUlHNk1Rc3dDUVlEVlFRR0V3SlZVekVSTUE4
           R0ExVUVDQXdJUTI5cwpiM0poWkc4eER6QU5CZ05WQkFjTUJrUmxiblpsY2pFVU1CSUdBMVVFQ2d3
           TFJYaGhiWEJzWlNCSmJtTXhHVEFYQmdOVkJBc01FRlZ1CmFXTnZjbTRnVkhKaGFXNWxjbk14S0RB
           bUJnTlZCQU1NSHlvdWFIUjBjSE10ZEdWemRDNTBhR1ZqWkc0dVpYaGhiWEJzWlM1amIyMHgKTERB
           cUJna3Foa2lHOXcwQkNRRVdIVzl3WlhKaGRHbHZibk5BZEdobFkyUnVMbVY0WVcxd2JHVXVZMjl0
           TUlJQklqQU5CZ2txaGtpRwo5dzBCQVFFRkFBT0NBUThBTUlJQkNnS0NBUUVBc2Vldm5NOTBWNmN6
           Ny9TSTNVNTlFRzIzRG9WRllvSEE2SHJkOTh4QmNpcHExcXlCCjB3NS9JZllWd0k5NFB3MDNDSitT
           a3VpMklkNFpwMythUlQ2Z054eDYxa2RwMWNVaHB0MVk4bGNVVklKckRRM0MzdmpCNkd3Y0tTWHMK
           RmxvS1BOUk1IVHcweEZ3NGdJQjlHeCtUenRhYlBzL1IraHAvbzlreWV1b0QyZm9HN3NQbkRWOU8y
           ZDVBOGJQWDhYWmNyZ045SnRwVApsd1E0NzVzUG9ZREJWVTJuY1dtWXl1N2JQbmZ2UFJQQm5jTVJI
           andLL2JGbG1LLytRTmJQTDBCQTV6aGRxREhETkYwNXFnZWhCN0VQCmdlOG5Za3BGdEtBamJpTVpB
           MVQvNFkxRy9iTEE5bFdXcUJFRVA4NmJYMkVxUmc5dmFJT1QyVE8wSCtQYzN0OHFVd0lEQVFBQk1B
           MEcKQ1NxR1NJYjNEUUVCQ3dVQUE0SUJBUUNyODJoU1hhNE9kNWRkQXpVMThzM3dnLzVlWmhxK3Bx
           bWx2NzFDdWpIL1A1NHUwUmtTNFUwLwo0SzhRQk8zdUZ0dHltclBqbjRvWUgyc2tiS1hHeDh0dkVu
           SENxTENPVlkrRzBDTUFkYkdKRTF6dFV1TEJ5RDBVQzY0TmphdDdweHlYCjFJNUI4UFRhZnZXMzdh
           cU1sUVFNR1JBay9iUVphUHRvRW1BcmdVZUUrZlFvQUNGQ2pZS3RUYVAvcDgxd3JlZldaczRJTktv
           MWJBVE8KajMwYTNKd2NXUXVuWkRNMklNUXdVUGNqYVh5ZnozQ1JqVHRaWjFJQ0dJeWpLYXU4SFh2
           WVVubm9XNjNsMVhZV09JRXJYa2J5ZVk0QwpTMTR3RGJLQUVmZk9QVGRCV2xweGJXSThtL094MHk4
           bVRBSlFXY2JTR3VUaVR6RlNWM0JpcDQrYTJQR04KLS0tLS1FTkQgQ0VSVElGSUNBVEUtLS0tLQo=
           """.trimIndent()

    // This is a test CA signed certificate chain created by hand where the expiration
    // date has already passed. So it cannot be used to create a false chain of trust.
    // https://jamielinux.com/docs/openssl-certificate-authority/index.html
    var MIME_ENCODED_CERT_CHAIN = """
           LS0tLS1CRUdJTiBDRVJUSUZJQ0FURS0tLS0tCk1JSUZtVENDQTRHZ0F3SUJBZ0lDRUFBd0RRWUpL
           b1pJaHZjTkFRRUxCUUF3WlRFTE1Ba0dBMVVFQmhNQ1ZWTXgKRVRBUEJnTlZCQWdNQ0VOdmJHOXlZ
           V1J2TVJBd0RnWURWUVFLREFkRGIyMWpZWE4wTVE4d0RRWURWUVFMREFaSgpVQ0JEUkU0eElEQWVC
           Z05WQkFNTUYwTnZiV05oYzNRZ1NXNTBaWEp0WldScFlYUmxJRU5CTUI0WERURTJNRGN5Ck9USXhN
           emN4TUZvWERURTJNRGN6TURJeE16Y3hNRm93Z1lZeEN6QUpCZ05WQkFZVEFsVlRNUkV3RHdZRFZR
           UUkKREFoRGIyeHZjbUZrYnpFUE1BMEdBMVVFQnd3R1JHVnVkbVZ5TVJBd0RnWURWUVFLREFkWGFX
           Um5aWFJ6TVJjdwpGUVlEVlFRTERBNVhhV1JuWlhRZ1YyRnphR1Z5Y3pFb01DWUdBMVVFQXd3Zktp
           NTBaWE4wTFdoMGRIQnpMblJvClpXTmtiaTVsZUdGdGNHeGxMbU52YlRDQ0FTSXdEUVlKS29aSWh2
           Y05BUUVCQlFBRGdnRVBBRENDQVFvQ2dnRUIKQUtiaEh2LzZYRGN3TkpHRzJVcnVaZmxybWVNLzFL
           aTRqOU8xdlcrWGtpbUI1K3N0RVo3aUJ4ZE1XbHorNlMyRgpMcWdVSXBwUjlFeU5mc3VvMDZiMUlO
           c2JDT29PZitqWnNRRHdTVlR6TTZJNkxQWkkrc2dMNFNmWk14V09Zbm5kCmhjUUdzT3JaeXNTYmtn
           bkJQM2xFQXlQWVovOThFSFBMOUJ1Sm9LUVVkQ1BqZjZYY2YxcGl1SVFnbVg4TStFMFAKZm5VMFRC
           Z2ZicUhCZ2srT1lzb21yNDhmVDl5SzdvbjFLUVVJb2V2aFBlbWlrai9HK1YyWWZtdTZIMU1HVkxm
           dwp6SmtHWXFFWVhsTHZhODNNYkxXNm1CeVBMbkQzVTJSWTZQNndGL0JBclR0emZBVDRMWEp0blBV
           YzNKZDR3UEd0Cmt3bldJWWtscWwwUjR6ZVdDRUxXeUprQ0F3RUFBYU9DQVM4d2dnRXJNQWtHQTFV
           ZEV3UUNNQUF3RVFZSllJWkkKQVliNFFnRUJCQVFEQWdaQU1ETUdDV0NHU0FHRytFSUJEUVFtRmlS
           UGNHVnVVMU5NSUVkbGJtVnlZWFJsWkNCVApaWEoyWlhJZ1EyVnlkR2xtYVdOaGRHVXdIUVlEVlIw
           T0JCWUVGRCtuWWk0NUpxaEVuVlpOZVp2aVNRSDVFVi85Ck1JR1JCZ05WSFNNRWdZa3dnWWFBRkxL
           R2k4NmFHTU5VeVVuczd1UDN6N2g0VzQ1d29XcWthREJtTVFzd0NRWUQKVlFRR0V3SlZVekVSTUE4
           R0ExVUVDQXdJUTI5c2IzSmhaRzh4RHpBTkJnTlZCQWNNQmtSbGJuWmxjakVRTUE0RwpBMVVFQ2d3
           SFEyOXRZMkZ6ZERFUE1BMEdBMVVFQ3d3R1NWQWdRMFJPTVJBd0RnWURWUVFEREFkRGIyMWpZWE4w
           CmdnSVFBREFPQmdOVkhROEJBZjhFQkFNQ0JhQXdFd1lEVlIwbEJBd3dDZ1lJS3dZQkJRVUhBd0V3
           RFFZSktvWkkKaHZjTkFRRUxCUUFEZ2dJQkFDNFJ4RjBXa2gwWmFjdmViUzJiVWtoNWIwZ1c0T2N4
           MUNKWXFvOVNKeVlNbStyVwpXOHVhRHljVXhYWlBsd3lOTCtpamxMYzY5VTZCTGszZThzbWFEekEx
           NDZ0MHJRUDQ5UHRNdXRxelNobHg5RXZECjVZa1NjbDNOUTVLQ0FqN05GR2s5ZGtrSnhha1lRNWRK
           WVYyZUV2Z1hZS0tKTU9ZMFZRQ3plVnJYMUthcDlqbVIKTUZYcDFCNGYwRHVsL0I2WUwxQWxDRHZZ
           TU02Q2ZWZkpRZlhhZ3BpK2owdWtrQnJnU0d5MHQvclVlZ09naW1kRApMSjdvZGxRcURWMnI4UCtB
           MklxRlhOajVqa29wVGhWV3FQajY0ZDVXU0J4NVhkbElXUmZZN1ZYajdqc1RuaGl6CmNPcmE0UkxL
           RXN5YnpTYklEcFl0U2FEUi8yNnZLbmFIOTFNKzV1QmpsODBNNGdHQ0RYSW1WSXRjU1JDY2VLamcK
           VE05VnU0ejZucmNHNXRVa3I5bSszbmhIRHk2UXdJMDFYTFphamQ3S25OSjRUMTB2dUtHZk5EQjVI
           cTdrb2xMWApTOEF2YVQ1b09ZN1pxWTd3Y1E1a1JHT0JKRHE1OUEvSWdvQTNnTzFIU29XYVp3cElV
           N3V3RTNhVFRFdnIrRGZYCkRkUUxQR3F5cHMyZkFlK1FCanVrNUI2ZitVd1FrY0l6Tk1TWEdpUWlo
           UXl1NERKUGZwTGdZRFVZeWM4VEx1UU0KazFJSVBJSnN1VTl6eUE5Uk9ud213dVBqSHVRaGRiVmVa
           R0RrRTRYQWFlVDZYa2JpUTNScmRDTmlrclZVNzY3aQphWmU1Tk5nRElFRnAydWJWcTBsN2JoNzRH
           K0VLN29xUGlRVGJOQzlkQUNvNjk4aVhacHZyYmdEeTBiVysKLS0tLS1FTkQgQ0VSVElGSUNBVEUt
           LS0tLQotLS0tLUJFR0lOIENFUlRJRklDQVRFLS0tLS0KTUlJRnJUQ0NBNVdnQXdJQkFnSUNFQUF3
           RFFZSktvWklodmNOQVFFTEJRQXdaakVMTUFrR0ExVUVCaE1DVlZNeApFVEFQQmdOVkJBZ01DRU52
           Ykc5eVlXUnZNUTh3RFFZRFZRUUhEQVpFWlc1MlpYSXhFREFPQmdOVkJBb01CME52CmJXTmhjM1F4
           RHpBTkJnTlZCQXNNQmtsUUlFTkVUakVRTUE0R0ExVUVBd3dIUTI5dFkyRnpkREFlRncweE5qQTMK
           TWpreU1USTBNek5hRncweE5qQTNNekF5TVRJME16TmFNR1V4Q3pBSkJnTlZCQVlUQWxWVE1SRXdE
           d1lEVlFRSQpEQWhEYjJ4dmNtRmtiekVRTUE0R0ExVUVDZ3dIUTI5dFkyRnpkREVQTUEwR0ExVUVD
           d3dHU1ZBZ1EwUk9NU0F3CkhnWURWUVFEREJkRGIyMWpZWE4wSUVsdWRHVnliV1ZrYVdGMFpTQkRR
           VENDQWlJd0RRWUpLb1pJaHZjTkFRRUIKQlFBRGdnSVBBRENDQWdvQ2dnSUJBTGs5Qk9vVk14RmRC
           WUdBaUZqZmx3UGE4UVpHQ1ZxVXluUFNZOFRRQnlSYQo5NktLVk5mb003MlRzeGZPcS80UXdPbXQ3
           NzF6RitEQWVRSkNpUmxWOWdwTFJPOURSRHFzVFpoazhWcW1QWktwCkgwcWhONnZxMnNmN0lQendt
           RlZaU1BWTXEyOWVjeEdwaSt3T0Qrdzg4aW92RkdQdmxYWTdESkV0Rkx2OHlpM0wKSi82NzZ1dTZ3
           alNoVDJSNDY5MEhRZU44b3c5NTJoVVFpNm1IRW01UC9VS0RxVEUvd2tnVStvcXlvSGVtS2lqUgpk
           M29LREYzYzNCdzV3TXpzVWkwczhEenEydEJKaHVxWEtjQzZVZ3J5eDhKdGRFa3RjRk53VElmM1BQ
           N2F1NGNMCm5ZeGpPZ1paczVrZEVaWUhCOTR2N0tKU1Zib2JKZVVleWJ3TzJqV2dJWXd0U2E4N3JK
           TWovbzQ5WWRMMWtVVk0KYjZjTHRPcXZ4S2orZXBBMEdFSFlFUmdxTzNXUy9oamRkVUNEVEZwY1Jh
           dDFaa0d0dFZ3VUxtanJCRkVxWkNzbwpXVVdvc3ovT0pTUU9IUjA4bENCUU83ZEVZQ0JlbXU0eUpy
           Slczbk9hOGlGTFMvQkVIczBBbEtrMXVkTlJXUEJPCjYySGVBcS9RWjVPaUFlaDNqeWlOYnhMQnEv
           NzgyTzB3Kzg0a1h2OE8rNm9jOEQ1M1FpbWo2WlpZNWJ5cmxiOUcKdGJ4elZnbDZRTzBZaDFSQ0E5
           NmVLY1FQR0ppZnZHMnZyemovR0R4ZUVyT0pQN3VyTjlRckwxOWVCVmtlK0Y3TQpqd0hnL0ZLMUZK
           aTA2RldpTVc2MXl0NHdYcjFiUVZRcFdhTDNWQUx4TEl3NmFOejkrZkJPZ0I3bFEvRlJBQ2Z2CkFn
           TUJBQUdqWmpCa01CMEdBMVVkRGdRV0JCU3lob3ZPbWhqRFZNbEo3TzdqOTgrNGVGdU9jREFmQmdO
           VkhTTUUKR0RBV2dCU2pVNnArdGdXdENZb3BuQzBhMFNLZWVhWlFrREFTQmdOVkhSTUJBZjhFQ0RB
           R0FRSC9BZ0VBTUE0RwpBMVVkRHdFQi93UUVBd0lCaGpBTkJna3Foa2lHOXcwQkFRc0ZBQU9DQWdF
           QUZqMy9EWEVhMVllTzVZWGJOVEc3Ck1TbnRhb2lkbVc3OHduK29UdjhGU25qZndTZnhMZGFlYm5I
           c3N0Y2owbGFwa1czclI0eVJHeFUzMWFJWWlSZHoKcHNqek9mcTdkUnA2WWlvY3pNaWUwTGJXbHRL
           MThNOFpYK3BIZnFxNjkxM3k2dzlrK2lGTDBZTVZIbTEzN2lnQQptQXE1aUdTT2RmMjRLUk1CMlJQ
           N0Z0SzVBSkFHSGpNL011aUIrNWFyVmh0ZzRrRGlLNWZDaiszaDY1bjVCeGVwClpUNmFrSnY4YXdy
           Ykx3MjY2bGp5bFQvejJkQllGOERSdG9oQTNraWVMZVAxczMwREUyU1ZDeVVzeElCU1FtUm0KSmhH
           K2xMUFJuSFAyT1FpU0JhRmZVT284YUkwYkxmbW9CMjlDR2RtbWE0dStOS0VqVFNmbDM1Ymx2MlIv
           eHJiaQowSnRmSU9XbytLTkU1ZFByVm1uYzJ0Tnc2WU9iampWL29yWXBQamhwdnBmMTUweCtvSUJs
           a0JaQzZ0cVlYRFVQCis4MW5ONGtFaW9FUUtjUjYxUFVFRE9vVE80TitkZ2h5RnhtVmtJbnlZeTdt
           UHFNWDJKUHZkeStLbXlPRzExeHYKSDdRUkF4MVQ3SWVEd2huRVdTRkZkODNEdXZRQTduTStBQ2Rx
           TE5EbTVvVGI0amRaVU8waVFoMm43em1iZkJWQgpTL1lLVExEWXNmbUpPVGdVM0VhWXdjSythcVk3
           OWxyY2VzZk5hY2g4NGxpTFhKMVRGN2VvN005ZVQ3eTVidEloCjRObmIrMlIxbkVHamFpNUpEdjhM
           RU9SRTRObGhsaFNuMEdhZHNHQlRhMkM1ZFVCaUZIb2NNYWwxSm96eUtKcjAKbVBXY3R0azEyWGVW
           Q2Y5ZkYvOWZPMFU9Ci0tLS0tRU5EIENFUlRJRklDQVRFLS0tLS0KLS0tLS1CRUdJTiBDRVJUSUZJ
           Q0FURS0tLS0tCk1JSUZzakNDQTVxZ0F3SUJBZ0lKQU5qRGt3dUFDV214TUEwR0NTcUdTSWIzRFFF
           QkN3VUFNR1l4Q3pBSkJnTlYKQkFZVEFsVlRNUkV3RHdZRFZRUUlEQWhEYjJ4dmNtRmtiekVQTUEw
           R0ExVUVCd3dHUkdWdWRtVnlNUkF3RGdZRApWUVFLREFkRGIyMWpZWE4wTVE4d0RRWURWUVFMREFa
           SlVDQkRSRTR4RURBT0JnTlZCQU1NQjBOdmJXTmhjM1F3CkhoY05NVFl3TnpJNU1qRXhOak14V2hj
           Tk1UWXdOek13TWpFeE5qTXhXakJtTVFzd0NRWURWUVFHRXdKVlV6RVIKTUE4R0ExVUVDQXdJUTI5
           c2IzSmhaRzh4RHpBTkJnTlZCQWNNQmtSbGJuWmxjakVRTUE0R0ExVUVDZ3dIUTI5dApZMkZ6ZERF
           UE1BMEdBMVVFQ3d3R1NWQWdRMFJPTVJBd0RnWURWUVFEREFkRGIyMWpZWE4wTUlJQ0lqQU5CZ2tx
           CmhraUc5dzBCQVFFRkFBT0NBZzhBTUlJQ0NnS0NBZ0VBcUFDSUNHbkVrMHZwdGNGUjVSUGNzWFN3
           eThPamZYdFAKdUxpYjZXbWJQd1lZQWEyUDQ1NUxPVlB4S2ZaYzVIOUJBVWswNUx6ZUk0VC82REUx
           NDlYY1lncE1pSXlJOXFSSgo0L1lJQ0FVNTJHSWkzY1FCeWl1VjdnNXEzUkxrM1dtZVhTTnVNeHFH
           ZVRpZzdxb2w1MUxJK2UvZ0NKdms2VnowCjlhektYVzkyYXUwV3V1VzMvUnVZRndYWWRpYUdZYVo3
           QVZ5cDZiMmUxeDJrdXBicXRQMDQ0czJkZXVSQzFtOWcKb3lUSlk1dHRta1E3ckxUc0czSDcyU01s
           T0xRb2czUUtZVmJNTUROZlFJRHpwVkpRbzI3QlQxRGY3ZnpuUWdacApMUG90djA2QitWb2doZGw3
           Zk1UZDAvWFhVUkk4S3ZUek1IakhCcDV5ZFpaUlVPMnlIR0N1Mm5uYmNuNm55MmNNCjE1aFQ3dHhp
           QWZlNVIvZUpQZDVabnBGeDB0TFkvZ1BPUDJMeU4xTmdsTFBSWWJFQkE3dXFJT3Z2QlpSY3dYbisK
           NzdGMWV4WHlreUNGNC8vVVF2ZHVJMndPazBaSVpURnVaaGRnajdMbVpOWlhoUUNsY3BXcDlaUnZM
           ekNjODlvRwo2M0lpV1B5RzdNSmRNUHppV0RoQ3JoYkVMVUo0akxRUzBHdDBYbWsvT1MwSitiTHhx
           SUZJYmRFNStUWW83K2c4ClNkdWxPUmp3cXRCYW5yeUMvbnlVb0JQSHQ3Q3JNeDlnUlE3NGFiM0s1
           OXd1OExhWnQ1MEptaEx2OWk1RDRWaDgKNktPdVlVRitZT3NQRlpwdzlGWFMxRy9lYkdxa2Qvekk0
           OFI2MVFESUN3TzF6RDdwYU82OVVhbVRKS29tRGs4Nwo4UGhvbWpXZ3VMVUNBd0VBQWFOak1HRXdI
           UVlEVlIwT0JCWUVGS05UcW42MkJhMEppaW1jTFJyUklwNTVwbENRCk1COEdBMVVkSXdRWU1CYUFG
           S05UcW42MkJhMEppaW1jTFJyUklwNTVwbENRTUE4R0ExVWRFd0VCL3dRRk1BTUIKQWY4d0RnWURW
           UjBQQVFIL0JBUURBZ0dHTUEwR0NTcUdTSWIzRFFFQkN3VUFBNElDQVFDaXJHTWJGa1lFRlNVbQo2
           TUJZTnVleUlYbUhNaEN6R1NOeU9UdWJsRDVtUVNEd2NEeTErS09wbGNLK3c5bzM3QnJzQlB6QVZX
           L0tjeUJSCjcwMmo0TjB4dG9mR1ExaGJXTnhYMjlSajVLWVhRVVZQRDYzdUxVaVRTc21tSjkxbndD
           N2hIOExLOTZFZWVkSHkKR0tVT1NLVjNreFV2UlFlLzk3Ykttbis5d2ZEMkJxd2hIM3E5Z1VUWkZy
           ellENnJvRGg4SHJOdFNndzJBSnVLKwpHM0NFWUdMUDU4MFZEcUhsL05VNWxPSVFmekdSbWN4elho
           M1JBVEpleE1yeFVsaHdpN2pwQ2dVeG5OWVFuUnBoCm11MVRjSXRJb0lDQXhER29kM0hGajFIWDhx
           aWVIU2ZXckZjU0tiZWdpVjRYbEI0eU1FcXdML0x0ems2RUVTdkUKL2FkdlR4dzY1T2htT1Vid2lY
           ck0vS1RTZDhwM0ltRFdjSlBoVXk1cHBjR0toVzJ5SWNSc1h5UnhWT2RsSTdnMQp0b1Q1SEc0Tm85
           U2RQSGNSTkVPakdGV3BkL1pyZ3ZsWGkyV3ZRYTNNSXpkYi9RV3IvcnRRUGpLSmorelFucFExCjdX
           NU9lOUc0NDJQaTZiWThDMEs4K1BjeUMrTkpVWm16MmdvT2hiMllzcFliaWkyMGpVSytNR2tONHZC
           ajlyWngKUUs1TEVpMUErZmVoVEI0eTN3bUczN3ZHVjYrNUxyU3ZyWlJnZHF1TWdrSXBHdHpyRC9N
           MWUrVVREdWY2bEhwVgplSjdDUW5LeHk4S3FnTVhodm5abVMxV0FGdGhSOUNuMzdxM1paay96ZUZv
           MXVrWTJiSE13SU1VWFQ1QWJod0ZyCnk5V3YzdkRJOGZWTHNpNlNBL1IyZ2lqbm9WbmcyUT09Ci0t
           LS0tRU5EIENFUlRJRklDQVRFLS0tLS0K
           """.trimIndent()
}