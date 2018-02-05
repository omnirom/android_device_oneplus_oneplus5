/*
 * Copyright (C) 2018 Jason A. Donenfeld <Jason@zx2c4.com>. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#include <stddef.h>
#include <dlfcn.h>

/* Real symbol lookups: */

static int (*_strncmp)(const char *, const char *, size_t);
static void shim_init(void) __attribute__((constructor))
{
	_strncmp = dlsym(RTLD_NEXT, "strncmp");
}

/* The shims: */

const char *_ZN7android18gClientPackageNameE;
const char *_ZN7android18lClientPackageNameE;

int strncmp(const char *first, const char *second, size_t n)
{
	if (!_strncmp("com.oneplus.camera", first, 18) || !_strncmp("com.oneplus.camera", second, 18))
		return 0;
	return _strncmp(first, second, n);
}
