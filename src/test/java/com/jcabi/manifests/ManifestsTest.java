/**
 * Copyright (c) 2012-2015, jcabi.com
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met: 1) Redistributions of source code must retain the above
 * copyright notice, this list of conditions and the following
 * disclaimer. 2) Redistributions in binary form must reproduce the above
 * copyright notice, this list of conditions and the following
 * disclaimer in the documentation and/or other materials provided
 * with the distribution. 3) Neither the name of the jcabi.com nor
 * the names of its contributors may be used to endorse or promote
 * products derived from this software without specific prior written
 * permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT
 * NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 * THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.jcabi.manifests;

import com.jcabi.log.Logger;
import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test case for {@link Manifests}.
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @since 0.7
 */
public final class ManifestsTest {

    /**
     * Manifests can read a single attribute, which always exist in MANIFEST.MF.
     * @throws Exception If something goes wrong
     */
    @Test
    public void readsSingleExistingAttribute() throws Exception {
        MatcherAssert.assertThat(
            Manifests.read("JCabi-Version"),
            Matchers.notNullValue()
        );
    }

    /**
     * Manifests can throw an exception if an attribute is empty.
     * @throws Exception If something goes wrong
     */
    @Test(expected = IllegalArgumentException.class)
    public void throwsExceptionWhenAttributeIsEmpty() throws Exception {
        Manifests.read("Jcabi-Test-Empty-Attribute");
    }

    /**
     * Manifests can throw an exception when attribute is absent.
     * @throws Exception If something goes wrong
     */
    @Test(expected = IllegalArgumentException.class)
    public void throwsExceptionIfAttributeIsMissed() throws Exception {
        Manifests.read("absent-property");
    }

    /**
     * Manifests can throw an exception loading file with empty attribute.
     * @throws Exception If something goes wrong
     */
    @Test(expected = IOException.class)
    public void throwsExceptionWhenNoAttributes() throws Exception {
        final File file = new File(
            Thread.currentThread().getContextClassLoader()
                .getResource("META-INF/MANIFEST_INVALID.MF").getFile()
        );
        final Manifests mfs = new Manifests();
        mfs.append(new FilesMfs(file));
    }

    /**
     * Manifests can append attributes from file.
     * @throws Exception If something goes wrong
     */
    @Test
    public void appendsAttributesFromFile() throws Exception {
        final String name = "Test-Attribute-From-File";
        final String value = "some text value of attribute";
        final File file = File.createTempFile("test-", ".MF");
        FileUtils.writeStringToFile(
            file,
            Logger.format("%s: %s\n", name, value)
        );
        final Manifests mfs = new Manifests();
        mfs.append(new FilesMfs(file));
        MatcherAssert.assertThat(
            "loaded from file",
            mfs.containsKey(name) && mfs.get(name).equals(value)
        );
    }

    /**
     * Manifests can append input stream.
     * @throws Exception If something goes wrong
     * @since 0.8
     */
    @Test
    public void appendsAttributesFromInputStream() throws Exception {
        final Manifests mfs = new Manifests();
        mfs.append(
            new StreamsMfs(this.getClass().getResourceAsStream("test.mf"))
        );
        MatcherAssert.assertThat(
            mfs.get("From-File"),
            Matchers.equalTo("some test attribute")
        );
    }

}
