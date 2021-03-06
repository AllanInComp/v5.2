// Copyright (c) 2006-2008 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

#include <windows.h>

#include "base/file_util.h"
#include "base/path_service.h"
#include "chrome/browser/navigation_entry.h"
#include "chrome/browser/session_backend.h"
#include "chrome/browser/session_service.h"
#include "chrome/browser/session_service_test_helper.h"
#include "chrome/common/chrome_paths.h"
#include "chrome/common/scoped_vector.h"
#include "chrome/common/stl_util-inl.h"
#include "testing/gtest/include/gtest/gtest.h"

class SessionServiceTest : public testing::Test {
 public:
  SessionServiceTest() : window_bounds(0, 1, 2, 3) {}

 protected:
  virtual void SetUp() {
    wchar_t b[32];
    _itow_s(static_cast<int>(GetTickCount()), b, arraysize(b), 10);

    PathService::Get(base::DIR_TEMP, &path_);
    file_util::AppendToPath(&path_, L"SessionTestDirs");
    file_util::CreateDirectory(path_);
    file_util::AppendToPath(&path_, b);

    helper_.set_service(new SessionService(path_));

    service()->SetWindowType(window_id, BrowserType::TABBED_BROWSER);
    service()->SetWindowBounds(window_id, window_bounds, false);
  }

  virtual void TearDown() {
    helper_.set_service(NULL);
    file_util::Delete(path_, true);
  }

  void UpdateNavigation(const SessionID& window_id,
                        const SessionID& tab_id,
                        const TabNavigation& navigation,
                        int index,
                        bool select) {
    NavigationEntry entry(TAB_CONTENTS_UNKNOWN_TYPE);
    entry.set_url(navigation.url);
    entry.set_referrer(navigation.referrer);
    entry.set_title(navigation.title);
    entry.set_content_state(navigation.state);
    entry.set_transition_type(navigation.transition);
    entry.set_has_post_data(
        navigation.type_mask & TabNavigation::HAS_POST_DATA);
    service()->UpdateTabNavigation(window_id, tab_id, index, entry);
    if (select)
      service()->SetSelectedNavigationIndex(window_id, tab_id, index);
  }

  void ReadWindows(std::vector<SessionWindow*>* windows) {
    // Forces closing the file.
    helper_.set_service(NULL);

    helper_.set_service(new SessionService(path_));
    helper_.ReadWindows(windows);
  }

  SessionService* service() { return helper_.service(); }

  SessionBackend* backend() { return helper_.backend(); }

  const gfx::Rect window_bounds;

  SessionID window_id;

  // Path used in testing.
  std::wstring path_;

  SessionServiceTestHelper helper_;
};

TEST_F(SessionServiceTest, Basic) {
  SessionID tab_id;
  ASSERT_NE(window_id.id(), tab_id.id());

  TabNavigation nav1(0, GURL("http://google.com"),
                     GURL("http://www.referrer.com"), L"abc", "def",
                     PageTransition::QUALIFIER_MASK);

  helper_.PrepareTabInWindow(window_id, tab_id, 0, true);
  UpdateNavigation(window_id, tab_id, nav1, 0, true);

  ScopedVector<SessionWindow> windows;
  ReadWindows(&(windows.get()));

  ASSERT_EQ(1, windows->size());
  ASSERT_TRUE(window_bounds == windows[0]->bounds);
  ASSERT_EQ(0, windows[0]->selected_tab_index);
  ASSERT_EQ(window_id.id(), windows[0]->window_id.id());
  ASSERT_EQ(1, windows[0]->tabs.size());
  ASSERT_EQ(BrowserType::TABBED_BROWSER, windows[0]->type);

  SessionTab* tab = windows[0]->tabs[0];
  helper_.AssertTabEquals(window_id, tab_id, 0, 0, 1, *tab);

  helper_.AssertNavigationEquals(nav1, tab->navigations[0]);
}

// Creates a navigation entry with post data, saves it, and makes sure it does
// not get restored.
TEST_F(SessionServiceTest, PrunePostData1) {
  SessionID tab_id;
  ASSERT_NE(window_id.id(), tab_id.id());

  TabNavigation nav1(0, GURL("http://google.com"), GURL(), L"abc", "def",
                     PageTransition::QUALIFIER_MASK);
  nav1.type_mask = TabNavigation::HAS_POST_DATA;

  helper_.PrepareTabInWindow(window_id, tab_id, 0, true);
  UpdateNavigation(window_id, tab_id, nav1, 0, true);

  ScopedVector<SessionWindow> windows;
  ReadWindows(&(windows.get()));

  ASSERT_EQ(0, windows->size());
}

// Creates two navigation entries, one with post data one without. Restores
// and verifies we get back only the entry with no post data.
TEST_F(SessionServiceTest, PrunePostData2) {
  SessionID tab_id;
  ASSERT_NE(window_id.id(), tab_id.id());

  TabNavigation nav1(0, GURL("http://google.com"),
                     GURL("http://www.referrer.com"), L"abc", "def",
                     PageTransition::QUALIFIER_MASK);
  nav1.type_mask = TabNavigation::HAS_POST_DATA;
  TabNavigation nav2(0, GURL("http://google2.com"), GURL(), L"abc", "def",
                     PageTransition::QUALIFIER_MASK);

  helper_.PrepareTabInWindow(window_id, tab_id, 0, true);
  UpdateNavigation(window_id, tab_id, nav1, 0, true);
  UpdateNavigation(window_id, tab_id, nav2, 1, false);

  ScopedVector<SessionWindow> windows;
  ReadWindows(&(windows.get()));

  ASSERT_EQ(1, windows->size());
  ASSERT_EQ(0, windows[0]->selected_tab_index);

  SessionTab* tab = windows[0]->tabs[0];
  helper_.AssertTabEquals(window_id, tab_id, 0, 0, 1, *tab);

  helper_.AssertNavigationEquals(nav2, tab->navigations[0]);
}

TEST_F(SessionServiceTest, ClosingTabStaysClosed) {
  SessionID tab_id;
  SessionID tab2_id;
  ASSERT_NE(tab_id.id(), tab2_id.id());

  TabNavigation nav1(0, GURL("http://google.com"), GURL(), L"abc", "def",
                     PageTransition::QUALIFIER_MASK);
  TabNavigation nav2(0, GURL("http://google2.com"), GURL(), L"abcd", "defg",
                     PageTransition::AUTO_BOOKMARK);

  helper_.PrepareTabInWindow(window_id, tab_id, 0, true);
  UpdateNavigation(window_id, tab_id, nav1, 0, true);

  helper_.PrepareTabInWindow(window_id, tab2_id, 1, false);
  UpdateNavigation(window_id, tab2_id, nav2, 0, true);
  service()->TabClosed(window_id, tab2_id);

  ScopedVector<SessionWindow> windows;
  ReadWindows(&(windows.get()));

  ASSERT_EQ(1, windows->size());
  ASSERT_EQ(0, windows[0]->selected_tab_index);
  ASSERT_EQ(window_id.id(), windows[0]->window_id.id());
  ASSERT_EQ(1, windows[0]->tabs.size());

  SessionTab* tab = windows[0]->tabs[0];
  helper_.AssertTabEquals(window_id, tab_id, 0, 0, 1, *tab);

  helper_.AssertNavigationEquals(nav1, tab->navigations[0]);
}

TEST_F(SessionServiceTest, Pruning) {
  SessionID tab_id;

  TabNavigation nav1(0, GURL("http://google.com"), GURL(), L"abc", "def",
                     PageTransition::QUALIFIER_MASK);
  TabNavigation nav2(0, GURL("http://google2.com"), GURL(), L"abcd", "defg",
                     PageTransition::AUTO_BOOKMARK);

  helper_.PrepareTabInWindow(window_id, tab_id, 0, true);
  for (int i = 0; i < 6; ++i) {
    TabNavigation& nav = (i % 2) == 0 ? nav1 : nav2;
    UpdateNavigation(window_id, tab_id, nav, i, true);
  }
  service()->TabNavigationPathPrunedFromBack(window_id, tab_id, 3);

  ScopedVector<SessionWindow> windows;
  ReadWindows(&(windows.get()));

  ASSERT_EQ(1, windows->size());
  ASSERT_EQ(0, windows[0]->selected_tab_index);
  ASSERT_EQ(1, windows[0]->tabs.size());

  SessionTab* tab = windows[0]->tabs[0];
  // We left the selected index at 5, then pruned. When rereading the
  // index should get reset to last valid navigation, which is 2.
  helper_.AssertTabEquals(window_id, tab_id, 0, 2, 3, *tab);

  helper_.AssertNavigationEquals(nav1, tab->navigations[0]);
  helper_.AssertNavigationEquals(nav2, tab->navigations[1]);
  helper_.AssertNavigationEquals(nav1, tab->navigations[2]);
}

TEST_F(SessionServiceTest, TwoWindows) {
  SessionID window2_id;
  SessionID tab1_id;
  SessionID tab2_id;

  TabNavigation nav1(0, GURL("http://google.com"), GURL(), L"abc", "def",
                     PageTransition::QUALIFIER_MASK);
  TabNavigation nav2(0, GURL("http://google2.com"), GURL(), L"abcd", "defg",
                     PageTransition::AUTO_BOOKMARK);

  helper_.PrepareTabInWindow(window_id, tab1_id, 0, true);
  UpdateNavigation(window_id, tab1_id, nav1, 0, true);

  const gfx::Rect window2_bounds(3, 4, 5, 6);
  service()->SetWindowType(window2_id, BrowserType::TABBED_BROWSER);
  service()->SetWindowBounds(window2_id, window2_bounds, true);
  helper_.PrepareTabInWindow(window2_id, tab2_id, 0, true);
  UpdateNavigation(window2_id, tab2_id, nav2, 0, true);

  ScopedVector<SessionWindow> windows;
  ReadWindows(&(windows.get()));

  ASSERT_EQ(2, windows->size());
  ASSERT_EQ(0, windows[0]->selected_tab_index);
  ASSERT_EQ(0, windows[1]->selected_tab_index);
  ASSERT_EQ(1, windows[0]->tabs.size());
  ASSERT_EQ(1, windows[1]->tabs.size());

  SessionTab* rt1;
  SessionTab* rt2;
  if (windows[0]->window_id.id() == window_id.id()) {
    ASSERT_EQ(window2_id.id(), windows[1]->window_id.id());
    ASSERT_FALSE(windows[0]->is_maximized);
    ASSERT_TRUE(windows[1]->is_maximized);
    rt1 = windows[0]->tabs[0];
    rt2 = windows[1]->tabs[0];
  } else {
    ASSERT_EQ(window2_id.id(), windows[0]->window_id.id());
    ASSERT_EQ(window_id.id(), windows[1]->window_id.id());
    ASSERT_TRUE(windows[0]->is_maximized);
    ASSERT_FALSE(windows[1]->is_maximized);
    rt1 = windows[1]->tabs[0];
    rt2 = windows[0]->tabs[0];
  }
  SessionTab* tab = rt1;
  helper_.AssertTabEquals(window_id, tab1_id, 0, 0, 1, *tab);
  helper_.AssertNavigationEquals(nav1, tab->navigations[0]);

  tab = rt2;
  helper_.AssertTabEquals(window2_id, tab2_id, 0, 0, 1, *tab);
  helper_.AssertNavigationEquals(nav2, tab->navigations[0]);
}

TEST_F(SessionServiceTest, WindowWithNoTabsGetsPruned) {
  SessionID window2_id;
  SessionID tab1_id;
  SessionID tab2_id;

  TabNavigation nav1(0, GURL("http://google.com"), GURL(), L"abc", "def",
                     PageTransition::QUALIFIER_MASK);

  helper_.PrepareTabInWindow(window_id, tab1_id, 0, true);
  UpdateNavigation(window_id, tab1_id, nav1, 0, true);

  const gfx::Rect window2_bounds(3, 4, 5, 6);
  service()->SetWindowType(window2_id, BrowserType::TABBED_BROWSER);
  service()->SetWindowBounds(window2_id, window2_bounds, false);
  helper_.PrepareTabInWindow(window2_id, tab2_id, 0, true);

  ScopedVector<SessionWindow> windows;
  ReadWindows(&(windows.get()));

  ASSERT_EQ(1, windows->size());
  ASSERT_EQ(0, windows[0]->selected_tab_index);
  ASSERT_EQ(1, windows[0]->tabs.size());
  ASSERT_EQ(window_id.id(), windows[0]->window_id.id());

  SessionTab* tab = windows[0]->tabs[0];
  helper_.AssertTabEquals(window_id, tab1_id, 0, 0, 1, *tab);
  helper_.AssertNavigationEquals(nav1, tab->navigations[0]);
}

TEST_F(SessionServiceTest, ClosingWindowDoesntCloseTabs) {
  SessionID tab_id;
  SessionID tab2_id;
  ASSERT_NE(tab_id.id(), tab2_id.id());

  TabNavigation nav1(0, GURL("http://google.com"), GURL(), L"abc", "def",
                     PageTransition::QUALIFIER_MASK);
  TabNavigation nav2(0, GURL("http://google2.com"), GURL(), L"abcd", "defg",
                     PageTransition::AUTO_BOOKMARK);

  helper_.PrepareTabInWindow(window_id, tab_id, 0, true);
  UpdateNavigation(window_id, tab_id, nav1, 0, true);

  helper_.PrepareTabInWindow(window_id, tab2_id, 1, false);
  UpdateNavigation(window_id, tab2_id, nav2, 0, true);

  service()->WindowClosing(window_id);

  ScopedVector<SessionWindow> windows;
  ReadWindows(&(windows.get()));

  ASSERT_EQ(1, windows->size());
  ASSERT_EQ(0, windows[0]->selected_tab_index);
  ASSERT_EQ(window_id.id(), windows[0]->window_id.id());
  ASSERT_EQ(2, windows[0]->tabs.size());

  SessionTab* tab = windows[0]->tabs[0];
  helper_.AssertTabEquals(window_id, tab_id, 0, 0, 1, *tab);
  helper_.AssertNavigationEquals(nav1, tab->navigations[0]);

  tab = windows[0]->tabs[1];
  helper_.AssertTabEquals(window_id, tab2_id, 1, 0, 1, *tab);
  helper_.AssertNavigationEquals(nav2, tab->navigations[0]);
}

TEST_F(SessionServiceTest, WindowCloseCommittedAfterNavigate) {
  SessionID window2_id;
  SessionID tab_id;
  SessionID tab2_id;
  ASSERT_NE(window2_id.id(), window_id.id());

  service()->SetWindowType(window2_id, BrowserType::TABBED_BROWSER);
  service()->SetWindowBounds(window2_id, window_bounds, false);

  TabNavigation nav1(0, GURL("http://google.com"), GURL(), L"abc", "def",
                     PageTransition::QUALIFIER_MASK);
  TabNavigation nav2(0, GURL("http://google2.com"), GURL(), L"abcd", "defg",
                     PageTransition::AUTO_BOOKMARK);

  helper_.PrepareTabInWindow(window_id, tab_id, 0, true);
  UpdateNavigation(window_id, tab_id, nav1, 0, true);

  helper_.PrepareTabInWindow(window2_id, tab2_id, 0, false);
  UpdateNavigation(window2_id, tab2_id, nav2, 0, true);

  service()->WindowClosing(window2_id);
  service()->TabClosed(window2_id, tab2_id);
  service()->WindowClosed(window2_id);

  ScopedVector<SessionWindow> windows;
  ReadWindows(&(windows.get()));

  ASSERT_EQ(1, windows->size());
  ASSERT_EQ(0, windows[0]->selected_tab_index);
  ASSERT_EQ(window_id.id(), windows[0]->window_id.id());
  ASSERT_EQ(1, windows[0]->tabs.size());

  SessionTab* tab = windows[0]->tabs[0];
  helper_.AssertTabEquals(window_id, tab_id, 0, 0, 1, *tab);
  helper_.AssertNavigationEquals(nav1, tab->navigations[0]);
}

// Makes sure we don't track popups.
TEST_F(SessionServiceTest, IgnorePopups) {
  SessionID window2_id;
  SessionID tab_id;
  SessionID tab2_id;
  ASSERT_NE(window2_id.id(), window_id.id());

  service()->SetWindowType(window2_id, BrowserType::BROWSER);
  service()->SetWindowBounds(window2_id, window_bounds, false);

  TabNavigation nav1(0, GURL("http://google.com"), GURL(), L"abc", "def",
                     PageTransition::QUALIFIER_MASK);
  TabNavigation nav2(0, GURL("http://google2.com"), GURL(), L"abcd", "defg",
                     PageTransition::AUTO_BOOKMARK);

  helper_.PrepareTabInWindow(window_id, tab_id, 0, true);
  UpdateNavigation(window_id, tab_id, nav1, 0, true);

  helper_.PrepareTabInWindow(window2_id, tab2_id, 0, false);
  UpdateNavigation(window2_id, tab2_id, nav2, 0, true);

  ScopedVector<SessionWindow> windows;
  ReadWindows(&(windows.get()));

  ASSERT_EQ(1, windows->size());
  ASSERT_EQ(0, windows[0]->selected_tab_index);
  ASSERT_EQ(window_id.id(), windows[0]->window_id.id());
  ASSERT_EQ(1, windows[0]->tabs.size());

  SessionTab* tab = windows[0]->tabs[0];
  helper_.AssertTabEquals(window_id, tab_id, 0, 0, 1, *tab);
  helper_.AssertNavigationEquals(nav1, tab->navigations[0]);
}

// Tests pruning from the front.
TEST_F(SessionServiceTest, PruneFromFront) {
  const std::string base_url("http://google.com/");
  SessionID tab_id;

  helper_.PrepareTabInWindow(window_id, tab_id, 0, true);

  // Add 5 navigations, with the 4th selected.
  for (int i = 0; i < 5; ++i) {
    TabNavigation nav(0, GURL(base_url + IntToString(i)), GURL(),
                      L"a", "b", PageTransition::QUALIFIER_MASK);
    UpdateNavigation(window_id, tab_id, nav, i, (i == 3));
  }

  // Prune the first two navigations from the front.
  helper_.service()->TabNavigationPathPrunedFromFront(window_id, tab_id, 2);

  // Read back in.
  ScopedVector<SessionWindow> windows;
  ReadWindows(&(windows.get()));

  ASSERT_EQ(1, windows->size());
  ASSERT_EQ(0, windows[0]->selected_tab_index);
  ASSERT_EQ(window_id.id(), windows[0]->window_id.id());
  ASSERT_EQ(1, windows[0]->tabs.size());

  // We should be left with three navigations, the 2nd selected.
  SessionTab* tab = windows[0]->tabs[0];
  ASSERT_EQ(1, tab->current_navigation_index);
  EXPECT_EQ(3U, tab->navigations.size());
  EXPECT_TRUE(GURL(base_url + IntToString(2)) == tab->navigations[0].url);
  EXPECT_TRUE(GURL(base_url + IntToString(3)) == tab->navigations[1].url);
  EXPECT_TRUE(GURL(base_url + IntToString(4)) == tab->navigations[2].url);
}

// Prunes from front so that we have no entries.
TEST_F(SessionServiceTest, PruneToEmpty) {
  const std::string base_url("http://google.com/");
  SessionID tab_id;

  helper_.PrepareTabInWindow(window_id, tab_id, 0, true);

  // Add 5 navigations, with the 4th selected.
  for (int i = 0; i < 5; ++i) {
    TabNavigation nav(0, GURL(base_url + IntToString(i)), GURL(),
                      L"a", "b", PageTransition::QUALIFIER_MASK);
    UpdateNavigation(window_id, tab_id, nav, i, (i == 3));
  }

  // Prune the first two navigations from the front.
  helper_.service()->TabNavigationPathPrunedFromFront(window_id, tab_id, 5);

  // Read back in.
  ScopedVector<SessionWindow> windows;
  ReadWindows(&(windows.get()));

  ASSERT_EQ(0, windows->size());
}
